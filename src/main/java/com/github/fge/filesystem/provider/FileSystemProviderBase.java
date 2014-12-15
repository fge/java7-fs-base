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
import com.github.fge.filesystem.options.FileSystemOptionsFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Base {@link FileSystemProvider} implementation
 *
 * <p><strong>Important note</strong>: in order to match the behaviour of the
 * default filesystems, you should be aware of the following:
 *
 * <ul>
 *     <li>The {@link #copy(Path, Path, CopyOption...) copy} operation will not
 *     perform recursive copies; if the source is a non empty directory, the
 *     operation fails with {@link DirectoryNotEmptyException}. This class
 *     enforces this behaviour if the source and target paths are not on the
 *     same {@link Path#getFileSystem() filesystem}; if they are, this is then
 *     delegated to {@link FileSystemDriver#copy(Path, Path, CopyOption...) your
 *     own copy implementation}.</li>
 *     <li>if the target path of a {@link #move(Path, Path, CopyOption...) move}
 *     operation exists and is a non empty directory, and you selected to
 *     replace the existing target path, the operation fails with {@link
 *     DirectoryNotEmptyException}; if the source and target paths are not on
 *     the same filesystem, this behaviour is enforced by this class; if they
 *     are on the same filesystem, this is delegated to {@link
 *     FileSystemDriver#move(Path, Path, CopyOption...) your own move
 *     implementation}.</li>
 *     <li>The deletion operation ({@link #delete(Path)} and {@link
 *     #deleteIfExists(Path)} do not perform recursive deletions; if the target
 *     is a non empty directory, this method throws {@link
 *     DirectoryNotEmptyException}.</li>
 * </ul>
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public abstract class FileSystemProviderBase
    extends FileSystemProvider
{
    private static final int BUFSIZE = 16384;
    private static final OpenOption[] NO_OPEN_OPTIONS = new OpenOption[0];

    protected final FileSystemRepository repository;

    protected FileSystemProviderBase(final FileSystemRepository repository)
    {
        this.repository = Objects.requireNonNull(repository);
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

    @Override
    public final Path getPath(final URI uri)
    {
        return repository.getPath(uri);
    }

    @Override
    public final InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        return driver.newInputStream(path, options);
    }

    @Override
    public final OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        return driver.newOutputStream(path, options);
    }

    @Override
    public final SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        return driver.newByteChannel(path, options, attrs);
    }

    @Override
    public final DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(dir);
        return driver.newDirectoryStream(dir, filter);
    }

    @Override
    public final void createDirectory(final Path dir,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        repository.getDriver(dir).createDirectory(dir, attrs);
    }

    @Override
    public final void delete(final Path path)
        throws IOException
    {
        repository.getDriver(path).delete(path);
    }

    @Override
    public final void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        final FileSystemDriver src = repository.getDriver(source);
        final FileSystemDriver dst = repository.getDriver(target);

        /*
         * If the same filesystem, call the (hopefully optimize) copy method
         * from the driver.
         */
        //noinspection ObjectEquality
        if (src == dst) {
            src.copy(source, target, options);
            return;
        }

        /*
         * Otherwise, translate the copy options and do a regular stream copy.
         */
        final OpenOption[] openOptions = copyToOpenOptions(options);
        try (
            final InputStream in = src.newInputStream(source);
            final OutputStream out = dst.newOutputStream(source, openOptions);
        ) {
            final byte[] buf = new byte[BUFSIZE];
            int bytesRead;

            while ((bytesRead = in.read(buf)) != -1)
                out.write(buf, 0, bytesRead);

            out.flush();
        }
    }

    @Override
    public final void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        // TODO: DirectoryNotEmptyException
        final FileSystemDriver src = repository.getDriver(source);
        final FileSystemDriver dst = repository.getDriver(target);

        /*
         * If the same filesystem, call the (hopefully optimize) move method
         * from the driver.
         */
        //noinspection ObjectEquality
        if (src == dst) {
            src.move(source, target, options);
            return;
        }

        /*
         * Otherwise, translate the copy options and do a regular stream copy.
         */
        final OpenOption[] openOptions = copyToOpenOptions(options);
        try (
            final InputStream in = src.newInputStream(source);
            final OutputStream out = dst.newOutputStream(source, openOptions);
        ) {
            final byte[] buf = new byte[BUFSIZE];
            int bytesRead;

            while ((bytesRead = in.read(buf)) != -1)
                out.write(buf, 0, bytesRead);

            out.flush();
        }

        src.delete(source);
    }

    @SuppressWarnings("ObjectEquality")
    @Override
    public final boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        final FileSystemDriver driver2 = repository.getDriver(path2);

        return driver == driver2 && driver.isSameFile(path, path2);
    }

    @Override
    public final boolean isHidden(final Path path)
        throws IOException
    {
        return repository.getDriver(path).isHidden(path);
    }

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
        return repository.getDriver(path)
            .getFileAttributeView(path, type, options);
    }

    @Override
    public final <A extends BasicFileAttributes> A readAttributes(
        final Path path, final Class<A> type, final LinkOption... options)
        throws IOException
    {
        final FileSystemDriver driver = repository.getDriver(path);
        return driver.readAttributes(path, type, options);
    }

    @Override
    public final Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        return repository.getDriver(path)
            .readAttributes(path, attributes, options);
    }

    @Override
    public final void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
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

    @SuppressWarnings({ "MethodMayBeStatic", "DesignForExtension" })
    protected OpenOption[] copyToOpenOptions(final CopyOption... copyOptions)
    {
        final Set<OpenOption> set = new HashSet<>();

        for (final CopyOption opt: copyOptions) {
            if (!(opt instanceof StandardCopyOption))
                throw new UnsupportedOperationException("unsupported option"
                    + " class " + opt.getClass().getCanonicalName());
            switch ((StandardCopyOption) opt) {
                case ATOMIC_MOVE:
                    throw new UnsupportedOperationException(opt + " not valid "
                        + "for copies");
                case COPY_ATTRIBUTES:
                    throw new UnsupportedOperationException(opt + " cannot be "
                        + "specified when copying across filesystems");
                case REPLACE_EXISTING:
                    set.add(StandardOpenOption.CREATE_NEW);
                    set.add(StandardOpenOption.WRITE);
            }
            if (opt.equals(StandardCopyOption.ATOMIC_MOVE))
                throw new UnsupportedOperationException(opt + " not valid"
                    + " for copies");
            if (opt.equals(StandardCopyOption.COPY_ATTRIBUTES))
                throw new UnsupportedOperationException(opt + " cannot be"
                    + " specified when copying across filesystems");
        }
        return set.isEmpty() ? NO_OPEN_OPTIONS
            : set.toArray(new OpenOption[set.size()]);
    }
}
