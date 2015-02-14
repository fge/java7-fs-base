/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.filesystem.provider;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.exceptions.IllegalOptionSetException;
import com.github.fge.filesystem.exceptions.UnsupportedOptionException;
import com.github.fge.filesystem.options.FileSystemOptionsFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Base {@link FileSystemProvider} implementation
 *
 * <p><strong>Important note</strong>: in order to match the behaviour of the
 * default filesystems, you should be aware of the following:</p>
 *
 * <ul>
 *     <li>The {@link #copy(Path, Path, CopyOption...) copy} operation will not
 *     perform recursive copies; if the source is a non empty directory, the
 *     operation fails with {@link DirectoryNotEmptyException}. This class
 *     enforces this behaviour if the source and target paths are not on the
 *     same {@link Path#getFileSystem() filesystem}; if they are, this is then
 *     delegated to {@link FileSystemDriver#copy(Path, Path, Set) your own copy
 *     implementation}.</li>
 *     <li>if the target path of a {@link #move(Path, Path, CopyOption...) move}
 *     operation exists and is a non empty directory, and you selected to
 *     replace the existing target path, the operation fails with {@link
 *     DirectoryNotEmptyException}; if the source and target paths are not on
 *     the same filesystem, this behaviour is enforced by this class; if they
 *     are on the same filesystem, this is delegated to {@link
 *     FileSystemDriver#move(Path, Path, Set) your own move implementation}.
 *     </li>
 *     <li>The deletion operation ({@link #delete(Path)} and {@link
 *     #deleteIfExists(Path)} do not perform recursive deletions; if the target
 *     is a non empty directory, this method throws {@link
 *     DirectoryNotEmptyException}.</li>
 * </ul>
 *
 * <p>All methods of this class which accept options as arguments will check the
 * validity of these options before performing the actual operation. Two
 * exceptions are possible when checking options:</p>
 *
 * <ul>
 *     <li>{@link UnsupportedOptionException},</li>
 *     <li>{@link IllegalOptionSetException}.</li>
 * </ul>
 *
 * @see FileSystemOptionsFactory
 * @see FileSystemDriver
 * @see FileSystemRepository#getDriver(Path)
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public abstract class FileSystemProviderBase
    extends FileSystemProvider
{
    private static final int BUFSIZE = 16384;

    protected final FileSystemRepository repository;
    protected final FileSystemOptionsFactory optionsFactory;

    protected FileSystemProviderBase(final FileSystemRepository repository)
    {
        this.repository = Objects.requireNonNull(repository);
        optionsFactory = repository.getFactoryProvider().getOptionsFactory();
    }

    @Override
    public final String getScheme()
    {
        return repository.getScheme();
    }

    @Override
    public final FileSystem newFileSystem(final URI uri,
        final Map<String, ?> env)
        throws IOException
    {
        final URI normalized = Objects.requireNonNull(uri).normalize();
        return repository.createFileSystem(this, normalized,
            Collections.unmodifiableMap(env));
    }

    @Override
    public final FileSystem getFileSystem(final URI uri)
    {
        return repository.getFileSystem(uri);
    }

    // TODO: this method is supposed to check the validity of the URI
    @Override
    public final Path getPath(final URI uri)
    {
        return repository.getPath(uri);
    }

    /**
     * Open an input stream to an existing path
     *
     * <p>This method checks the existence of the file before delegating the
     * creation of the input stream to the relevant driver. The driver is also
     * responsible to deal with the target not being a directory.</p>
     *
     * @param path the path to open
     * @param options open options
     * @return an input stream
     * @throws NoSuchFileException file does not exist
     * @throws IOException other I/O exception
     *
     * @see FileSystemDriver#newInputStream(Path, Set)
     */
    @Override
    public final InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final Set<OpenOption> optionSet
            = optionsFactory.compileReadOptions(options);
        final FileSystemDriver driver = repository.getDriver(path);

        driver.checkAccess(path);

        return driver.newInputStream(path, optionSet);
    }

    /**
     * Open an output stream to a given path
     *
     * <p>This class performs the following checks before delegating to the
     * driver:</p>
     *
     * <ul>
     *     <li>if {@link StandardOpenOption#CREATE_NEW} is specified and the
     *     file already exists, throws {@link FileAlreadyExistsException};</li>
     *     <li>if the file does not exist and {@link
     *     StandardOpenOption#CREATE} is not set, throws {@link
     *     NoSuchFileException};</li>
     *     <li>if the file exists and cannot be written to, throws {@link
     *     AccessDeniedException}.</li>
     * </ul>
     *
     * <p>All other checks, such as for instance the target being a directory
     * and not a file, are left to the driver.</p>
     *
     * @param path the path to open
     * @param options the set of open options
     * @return an output stream
     * @throws FileAlreadyExistsException {@link StandardOpenOption#CREATE_NEW}
     * was specified but the target already exists
     * @throws AccessDeniedException target exists but cannot be written to
     * @throws IOException other I/O exception
     *
     * @see FileSystemDriver#newOutputStream(Path, Set)
     */
    @Override
    public final OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final Set<OpenOption> optionSet
            = optionsFactory.compileWriteOptions(options);
        final FileSystemDriver driver = repository.getDriver(path);

        try {
            driver.checkAccess(path, AccessMode.WRITE);
            if (optionSet.contains(StandardOpenOption.CREATE_NEW))
                throw new FileAlreadyExistsException(path.toString());
        } catch (NoSuchFileException ignored) {
            // The set will always contain at least CREATE
        }

        return driver.newOutputStream(path, optionSet);
    }

    /**
     * TODO: throws UnsupportedOperationException if attributes are specified
     *
     * @param path the path to open a channel to
     * @param options the set of options
     * @param attrs file attributes to set if file is created
     * @return a new channel
     * @throws IOException error creating the channel
     *
     * @see FileSystemDriver#newByteChannel(Path, Set, FileAttribute[])
     */
    @Override
    public final SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        // TODO: check options
        if (attrs.length != 0)
            throw new UnsupportedOperationException("TODO");
        final FileSystemDriver driver = repository.getDriver(path);
        // TODO: check existence/creation
        return driver.newByteChannel(path, options, attrs);
    }

    /**
     * Open a new directory stream from a target path
     *
     * <p>This method checks that the target path exists and that the process
     * has read access to it before delegating the directory stream creation to
     * the driver.</p>
     *
     * <p>It is up to the driver to perform other checks (such as whether the
     * target path is indeed a directory).</p>
     *
     * @param dir the path
     * @param filter a filter for directory entries
     * @return a directory stream
     * @throws NoSuchFileException target path does not exist
     * @throws IOException other I/O exception
     *
     * @see FileSystemDriver#newDirectoryStream(Path, DirectoryStream.Filter)
     */
    @Override
    public final DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        // TODO: EXECUTE permission not checked; unneeded on Unix. Others?
        final FileSystemDriver driver = repository.getDriver(dir);
        driver.checkAccess(dir, AccessMode.READ);
        return driver.newDirectoryStream(dir, filter);
    }

    /**
     * Create a directory
     *
     * <p>TODO: UnsupportedOperationException if any attributes are specified
     * </p>
     *
     * @param dir the directory to create
     * @param attrs attributes to the created directory
     * @throws FileAlreadyExistsException path already exists (whether it is
     * a directory or not)
     * @throws IOException directory creation failed
     *
     * @see FileSystemDriver#createDirectory(Path, FileAttribute[])
     */
    @Override
    public final void createDirectory(final Path dir,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        // TODO
        if (attrs.length != 0)
            throw new UnsupportedOperationException("TODO");

        final FileSystemDriver driver = repository.getDriver(dir);

        try {
            checkAccess(dir);
            throw new FileAlreadyExistsException(dir.toString());
        } catch (NoSuchFileException ignored) {
            /*
             * We only ignore the exception if the entry does NOT exist; any
             * other IOException is a problem, so let it through
             */
        }

        driver.createDirectory(dir, attrs);
    }

    /**
     * Delete an entry on the filesystem
     *
     * <p>This method will check whether the path actually exists before
     * delegating to the driver.</p>
     *
     * <p><strong>Recall:</strong> this operation does <em>not</em> perform
     * recursive deletions. It is up to the driver to check whether the target
     * is a non empty directory, and fail with {@link
     * DirectoryNotEmptyException} if this is the case.</p>
     *
     * @param path the path to delete
     * @throws NoSuchFileException target does not exist
     * @throws IOException other I/O error
     *
     * @see FileSystemDriver#delete(Path)
     */
    @Override
    public final void delete(final Path path)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        driver.checkAccess(path);
        driver.delete(path);
    }

    /**
     * Copy a source path to a target path
     *
     * <p>This method will do the following before performing the actual copy:
     * </p>
     *
     * <ul>
     *     <li>check that the source path exists;</li>
     *     <li>if {@link StandardCopyOption#REPLACE_EXISTING} is not set, check
     *     that the destination path <em>does not</em> exist.</li>
     * </ul>
     *
     * <p>Il will then delegate to the relevant driver if and only if both paths
     * are issued from the same {@link FileSystem}. If not, it performs the copy
     * itself.</p>
     *
     * <p>Note that recursive copies are NOT performed by this method.
     * Similarly, the driver SHOULD NOT perform recursive copies.</p>
     *
     * @param source the source path
     * @param target the target path
     * @param options the copy options
     * @throws NoSuchFileException source path does not exist
     * @throws FileAlreadyExistsException destination path exists and {@link
     * StandardCopyOption#REPLACE_EXISTING} was not set
     * @throws IOException other I/O error
     *
     * @see FileSystemDriver#copy(Path, Path, Set)
     */
    @Override
    public final void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        final Set<CopyOption> optionSet
            = optionsFactory.compileCopyOptions(options);

        final FileSystemDriver src = repository.getDriver(source);
        final FileSystemDriver dst = repository.getDriver(target);

        src.checkAccess(source);
        try {
            dst.checkAccess(target);
            if (!optionSet.contains(StandardCopyOption.REPLACE_EXISTING))
                throw new FileAlreadyExistsException(target.toString());
        } catch (NoSuchFileException ignored) {
        }

        /*
         * If the same filesystem, call the (hopefully optimize) copy method
         * from the driver.
         */
        //noinspection ObjectEquality
        if (src == dst) {
            src.copy(source, target, optionSet);
            return;
        }

        /*
         * Otherwise, translate the copy options and do a regular stream copy.
         */
        final Set<OpenOption> readOptions
            = optionsFactory.toReadOptions(optionSet);
        final Set<OpenOption> writeOptions
            = optionsFactory.toWriteOptions(optionSet);

        try (
            /*
             * It is delegated to the drivers to see whether the source or
             * target are directories
             */
            final InputStream in = src.newInputStream(source, readOptions);
            final OutputStream out = dst.newOutputStream(source, writeOptions);
        ) {
            final byte[] buf = new byte[BUFSIZE];
            int bytesRead;

            while ((bytesRead = in.read(buf)) != -1)
                out.write(buf, 0, bytesRead);

            out.flush();
        }
    }

    /**
     * Move a source path to a target path
     *
     * <p>This method will perform the following checks</p>
     *
     * <ul>
     *     <li>if the source does not exist, throws {@link
     *     NoSuchFileException};</li>
     *     <li>if the target exists and {@link
     *     StandardCopyOption#REPLACE_EXISTING} is not set, throws {@link
     *     FileAlreadyExistsException}.</li>
     * </ul>
     *
     * <p>From this point on, it will delegate to the driver <em>if and only
     * if</em> both the source and target are on the same filesystem. If this
     * is not the case, TODO: implement metadata driver, for now it sucks</p>
     *
     * @param source the path to move
     * @param target the destination path
     * @param options the set of copy options
     * @throws NoSuchFileException the source does not exist
     * @throws FileAlreadyExistsException the target exists and {@link
     * StandardCopyOption#REPLACE_EXISTING} was not set
     * @throws IOException other I/O exception
     *
     * @see FileSystemDriver#move(Path, Path, Set)
     */
    @Override
    public final void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        final Set<CopyOption> optionSet
            = optionsFactory.compileCopyOptions(options);

        final FileSystemDriver src = repository.getDriver(source);
        final FileSystemDriver dst = repository.getDriver(target);

        /*
         * If the same filesystem, call the (hopefully optimize) move method
         * from the driver.
         */
        //noinspection ObjectEquality
        if (src == dst) {
            src.move(source, target, optionSet);
            return;
        }

        /*
         * Otherwise, translate the copy options and do a regular stream copy.
         */
        // TODO!!
        final Set<OpenOption> readOptions
            = optionsFactory.toReadOptions(optionSet);
        final Set<OpenOption> writeOptions
            = optionsFactory.toWriteOptions(optionSet);
        try (
            final InputStream in = src.newInputStream(source, readOptions);
            final OutputStream out = dst.newOutputStream(source, writeOptions);
        ) {
            final byte[] buf = new byte[BUFSIZE];
            int bytesRead;

            while ((bytesRead = in.read(buf)) != -1)
                out.write(buf, 0, bytesRead);

            out.flush();
        }

        src.delete(source);
    }

    /**
     * Tell whether two paths represent exactly the same filesystem objects
     *
     * <p>This method checks that both paths exist and have the same driver
     * (which is the same as checking whether they are on the same filesystem,
     * since no two filesystems share a driver), and then delegates to the
     * driver to perform additional checks.</p>
     *
     * @param path the first path
     * @param path2 the second path
     * @return true if and only if both paths point to the same filesystem
     * object
     * @throws NoSuchFileException one, or both, paths do not exist
     * @throws IOException error processing one, or both, paths
     *
     * @see FileSystemDriver#isSameFile(Path, Path)
     */
    @SuppressWarnings("ObjectEquality")
    @Override
    public final boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        final FileSystemDriver driver2 = repository.getDriver(path2);

        if (driver != driver2)
            return false;

        driver.checkAccess(path);
        driver.checkAccess(path2);

        return driver.isSameFile(path, path2);
    }

    /**
     * Tell whether the filesystem object pointed by the path (if any) is hidden
     *
     * <p>Typically, on Unix systems, this will be for paths whose last name
     * component (if any) starts with a dot.</p>
     *
     * @param path the path to check
     * @return true if the file is considered hidden
     * @throws IOException error accessing path information
     *
     * @see FileSystemDriver#isSameFile(Path, Path)
     */
    @Override
    public final boolean isHidden(final Path path)
        throws IOException
    {
        return repository.getDriver(path).isHidden(path);
    }

    /**
     * Check whether a path exists and can be accessed using the given modes
     *
     * <p>Passing no modes argument typically only makes an existence check.</p>
     *
     * @param path the path to check
     * @param modes the modes to check against
     * @throws IOException error accessing path information
     *
     * @see FileSystemDriver#checkAccess(Path, AccessMode...)
     */
    @Override
    public final void checkAccess(final Path path, final AccessMode... modes)
        throws IOException
    {
        repository.getDriver(path).checkAccess(path, modes);
    }

    @Override
    public final <V extends FileAttributeView> V getFileAttributeView(
        final Path path, final Class<V> type, final LinkOption... options)
    {
        optionsFactory.checkLinkOptions(options);
        return repository.getDriver(path)
            .getFileAttributeView(path, type, options);
    }

    @Override
    public final <A extends BasicFileAttributes> A readAttributes(
        final Path path, final Class<A> type, final LinkOption... options)
        throws IOException
    {
        optionsFactory.checkLinkOptions(options);
        final FileSystemDriver driver = repository.getDriver(path);
        return driver.readAttributes(path, type, options);
    }

    @Override
    public final Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        optionsFactory.checkLinkOptions(options);
        return repository.getDriver(path)
            .readAttributes(path, attributes, options);
    }

    @Override
    public final void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        optionsFactory.checkLinkOptions(options);
        repository.getDriver(path)
            .setAttribute(path, attribute, value, options);
    }

    @Override
    public final FileStore getFileStore(final Path path)
        throws IOException
    {
        // See GenericFileSystem: only one file store per filesystem
        return path.getFileSystem().getFileStores().iterator().next();
    }
}
