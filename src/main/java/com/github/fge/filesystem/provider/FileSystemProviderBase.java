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
import com.github.fge.filesystem.fs.FileSystemBase;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
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
 * <p>Notes:</p>
 *
 * <ul>
 *     <li>{@link #getPath(URI)}: filesystems are never created automatically;
 *     </li>
 *     <li>{@link #newByteChannel(Path, Set, FileAttribute[])}: throws {@link
 *     UnsupportedOperationException};</li>
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

    /**
     * Returns the URI scheme that identifies this provider.
     *
     * @return The URI scheme
     */
    @Override
    public final String getScheme()
    {
        return repository.getScheme();
    }

    /**
     * Constructs a new {@code FileSystem} object identified by a URI. This
     * method is invoked by the {@link FileSystems#newFileSystem(URI, Map)}
     * method to open a new file system identified by a URI.
     * <p> The {@code uri} parameter is an absolute, hierarchical URI, with a
     * scheme equal (without regard to case) to the scheme supported by this
     * provider. The exact form of the URI is highly provider dependent. The
     * {@code env} parameter is a map of provider specific properties to
     * configure
     * the file system.
     * <p> This method throws {@link FileSystemAlreadyExistsException} if the
     * file system already exists because it was previously created by an
     * invocation of this method. Once a file system is {@link
     * FileSystem#close closed} it is provider-dependent if the
     * provider allows a new file system to be created with the same URI as a
     * file system it previously created.
     *
     * @param uri URI reference
     * @param env A map of provider specific properties to configure the file
     * system;
     * may be empty
     * @return A new file system
     *
     * @throws IllegalArgumentException If the pre-conditions for the {@code
     * uri} parameter aren't met,
     * or the {@code env} parameter does not contain properties required
     * by the provider, or a property value is invalid
     * @throws IOException An I/O error occurs creating the file system
     * @throws SecurityException If a security manager is installed and it
     * denies an unspecified
     * permission required by the file system provider implementation
     * @throws FileSystemAlreadyExistsException If the file system has
     * already been created
     */
    @Override
    public final FileSystem newFileSystem(final URI uri,
        final Map<String, ?> env)
        throws IOException
    {
        final URI normalized = Objects.requireNonNull(uri).normalize();
        return repository.createFileSystem(this, normalized,
            Collections.unmodifiableMap(env));
    }

    /**
     * Returns an existing {@code FileSystem} created by this provider.
     * <p> This method returns a reference to a {@code FileSystem} that was
     * created by invoking the {@link #newFileSystem(URI, Map) newFileSystem
     * (URI,Map)}
     * method. File systems created the {@link #newFileSystem(Path, Map)
     * newFileSystem(Path,Map)} method are not returned by this method.
     * The file system is identified by its {@code URI}. Its exact form
     * is highly provider dependent. In the case of the default provider the
     * URI's
     * path component is {@code "/"} and the authority, query and fragment
     * components
     * are undefined (Undefined components are represented by {@code null}).
     * <p> Once a file system created by this provider is {@link
     * FileSystem#close closed} it is provider-dependent if this
     * method returns a reference to the closed file system or throws {@link
     * FileSystemNotFoundException}. If the provider allows a new file system to
     * be created with the same URI as a file system it previously created then
     * this method throws the exception if invoked after the file system is
     * closed (and before a new instance is created by the {@link #newFileSystem
     * newFileSystem} method).
     * <p> If a security manager is installed then a provider implementation
     * may require to check a permission before returning a reference to an
     * existing file system. In the case of the {@link FileSystems#getDefault
     * default} file system, no permission check is required.
     *
     * @param uri URI reference
     * @return The file system
     *
     * @throws IllegalArgumentException If the pre-conditions for the {@code
     * uri} parameter aren't met
     * @throws FileSystemNotFoundException If the file system does not exist
     * @throws SecurityException If a security manager is installed and it
     * denies an unspecified
     * permission.
     */
    @Override
    public final FileSystem getFileSystem(final URI uri)
    {
        return repository.getFileSystem(uri);
    }

    /**
     * Return a {@code Path} object by converting the given {@link URI}. The
     * resulting {@code Path} is associated with a {@link FileSystem} that
     * already exists or is constructed automatically.
     * <p> The exact form of the URI is file system provider dependent. In the
     * case of the default provider, the URI scheme is {@code "file"} and the
     * given URI has a non-empty path component, and undefined query, and
     * fragment components. The resulting {@code Path} is associated with the
     * default {@link FileSystems#getDefault default} {@code FileSystem}.
     * <p> If a security manager is installed then a provider implementation
     * may require to check a permission. In the case of the {@link
     * FileSystems#getDefault default} file system, no permission check is
     * required.
     *
     * @param uri The URI to convert
     * @throws IllegalArgumentException If the URI scheme does not identify
     * this provider or other
     * preconditions on the uri parameter do not hold
     * @throws FileSystemNotFoundException The file system, identified by the
     * URI, does not exist and
     * cannot be created automatically
     * @throws SecurityException If a security manager is installed and it
     * denies an unspecified
     * permission.
     */
    @Override
    public final Path getPath(final URI uri)
    {
        return repository.getPath(uri);
    }

    /**
     * Opens a file, returning an input stream to read from the file. This
     * method works in exactly the manner specified by the {@link
     * Files#newInputStream} method.
     * <p> The default implementation of this method opens a channel to the file
     * as if by invoking the {@link #newByteChannel} method and constructs a
     * stream that reads bytes from the channel. This method should be
     * overridden
     * where appropriate.
     *
     * @param path the path to the file to open
     * @param options options specifying how the file is opened
     * @return a new input stream
     *
     * @throws IllegalArgumentException if an invalid combination of options
     * is specified
     * @throws UnsupportedOperationException if an unsupported option is
     * specified
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file.
     */
    @Override
    public final InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(path);
        return driver.newInputStream(path, options);
    }

    /**
     * Opens or creates a file, returning an output stream that may be used to
     * write bytes to the file. This method works in exactly the manner
     * specified by the {@link Files#newOutputStream} method.
     * <p> The default implementation of this method opens a channel to the file
     * as if by invoking the {@link #newByteChannel} method and constructs a
     * stream that writes bytes to the channel. This method should be overridden
     * where appropriate.
     *
     * @param path the path to the file to open or create
     * @param options options specifying how the file is opened
     * @return a new output stream
     *
     * @throws IllegalArgumentException if {@code options} contains an
     * invalid combination of options
     * @throws UnsupportedOperationException if an unsupported option is
     * specified
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkWrite(String) checkWrite}
     * method is invoked to check write access to the file. The {@link
     * SecurityManager#checkDelete(String) checkDelete} method is
     * invoked to check delete access if the file is opened with the
     * {@code DELETE_ON_CLOSE} option.
     */
    @Override
    public final OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(path);
        return driver.newOutputStream(path, options);
    }

    /**
     * Opens or creates a file, returning a seekable byte channel to access the
     * file. This method works in exactly the manner specified by the {@link
     * Files#newByteChannel(Path, Set, FileAttribute[])} method.
     *
     * @param path the path to the file to open or create
     * @param options options specifying how the file is opened
     * @param attrs an optional list of file attributes to set atomically when
     * creating the file
     * @return a new seekable byte channel
     *
     * @throws IllegalArgumentException if the set contains an invalid
     * combination of options
     * @throws UnsupportedOperationException if an unsupported open option is
     * specified or the array contains
     * attributes that cannot be set atomically when creating the file
     * @throws FileAlreadyExistsException if a file of that name already
     * exists and the {@link
     * StandardOpenOption#CREATE_NEW CREATE_NEW} option is specified
     * <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the path if the file is
     * opened for reading. The {@link SecurityManager#checkWrite(String)
     * checkWrite} method is invoked to check write access to the path
     * if the file is opened for writing. The {@link
     * SecurityManager#checkDelete(String) checkDelete} method is
     * invoked to check delete access if the file is opened with the
     * {@code DELETE_ON_CLOSE} option.
     */
    @Override
    public final SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(path);
        return driver.newByteChannel(path, options, attrs);
    }

    /**
     * Opens a directory, returning a {@code DirectoryStream} to iterate over
     * the entries in the directory. This method works in exactly the manner
     * specified by the {@link
     * Files#newDirectoryStream(Path, DirectoryStream.Filter)}
     * method.
     *
     * @param dir the path to the directory
     * @param filter the directory stream filter
     * @return a new and open {@code DirectoryStream} object
     *
     * @throws NotDirectoryException if the file could not otherwise be
     * opened because it is not
     * a directory <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the directory.
     */
    @Override
    public final DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(dir);
        return driver.newDirectoryStream(dir, filter);
    }

    /**
     * Creates a new directory. This method works in exactly the manner
     * specified by the {@link Files#createDirectory} method.
     *
     * @param dir the directory to create
     * @param attrs an optional list of file attributes to set atomically when
     * creating the directory
     * @throws UnsupportedOperationException if the array contains an
     * attribute that cannot be set atomically
     * when creating the directory
     * @throws FileAlreadyExistsException if a directory could not otherwise
     * be created because a file of
     * that name already exists <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs or the parent directory
     * does not exist
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkWrite(String) checkWrite}
     * method is invoked to check write access to the new directory.
     */
    @Override
    public final void createDirectory(final Path dir,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        getDriver(dir).createDirectory(dir, attrs);
    }

    /**
     * Deletes a file. This method works in exactly the  manner specified by the
     * {@link Files#delete} method.
     *
     * @param path the path to the file to delete
     * @throws NoSuchFileException if the file does not exist <i>(optional
     * specific exception)</i>
     * @throws DirectoryNotEmptyException if the file is a directory and
     * could not otherwise be deleted
     * because the directory is not empty <i>(optional specific
     * exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkDelete(String)} method
     * is invoked to check delete access to the file
     */
    @Override
    public final void delete(final Path path)
        throws IOException
    {
        getDriver(path).delete(path);
    }

    /**
     * Copy a file to a target file. This method works in exactly the manner
     * specified by the {@link Files#copy(Path, Path, CopyOption[])} method
     * except that both the source and target paths must be associated with
     * this provider.
     *
     * @param source the path to the file to copy
     * @param target the path to the target file
     * @param options options specifying how the copy should be done
     * @throws UnsupportedOperationException if the array contains a copy
     * option that is not supported
     * @throws FileAlreadyExistsException if the target file exists but
     * cannot be replaced because the
     * {@code REPLACE_EXISTING} option is not specified <i>(optional
     * specific exception)</i>
     * @throws DirectoryNotEmptyException the {@code REPLACE_EXISTING} option
     * is specified but the file
     * cannot be replaced because it is a non-empty directory
     * <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the source file, the
     * {@link SecurityManager#checkWrite(String) checkWrite} is invoked
     * to check write access to the target file. If a symbolic link is
     * copied the security manager is invoked to check {@link
     * LinkPermission}{@code ("symbolic")}.
     */
    @Override
    public final void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        final FileSystemDriver src = getDriver(source);
        final FileSystemDriver dst = getDriver(target);

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

    /**
     * Move or rename a file to a target file. This method works in exactly the
     * manner specified by the {@link Files#move} method except that both the
     * source and target paths must be associated with this provider.
     *
     * @param source the path to the file to move
     * @param target the path to the target file
     * @param options options specifying how the move should be done
     * @throws UnsupportedOperationException if the array contains a copy
     * option that is not supported
     * @throws FileAlreadyExistsException if the target file exists but
     * cannot be replaced because the
     * {@code REPLACE_EXISTING} option is not specified <i>(optional
     * specific exception)</i>
     * @throws DirectoryNotEmptyException the {@code REPLACE_EXISTING} option
     * is specified but the file
     * cannot be replaced because it is a non-empty directory
     * <i>(optional specific exception)</i>
     * @throws AtomicMoveNotSupportedException if the options array contains
     * the {@code ATOMIC_MOVE} option but
     * the file cannot be moved as an atomic file system operation.
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkWrite(String) checkWrite}
     * method is invoked to check write access to both the source and
     * target file.
     */
    @Override
    public final void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        // TODO: DirectoryNotEmptyException
        final FileSystemDriver src = getDriver(source);
        final FileSystemDriver dst = getDriver(target);

        /*
         * If the same filesystem, call the (hopefully optimize) move method
         * from the driver.
         */
        //noinspection ObjectEquality
        if (src == dst) {
            src.move(source, target, options);
            return;
        }

        Files.walkFileTree(source, new MoveVisitor(src, source, dst, target));
    }

    /**
     * Tests if two paths locate the same file. This method works in exactly the
     * manner specified by the {@link Files#isSameFile} method.
     *
     * @param path one path to the file
     * @param path2 the other path
     * @return {@code true} if, and only if, the two paths locate the same file
     *
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to both files.
     */
    @SuppressWarnings("ObjectEquality")
    @Override
    public final boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(path);
        final FileSystemDriver driver2 = getDriver(path2);

        return driver == driver2 && driver.isSameFile(path, path2);
    }

    /**
     * Tells whether or not a file is considered <em>hidden</em>. This method
     * works in exactly the manner specified by the {@link Files#isHidden}
     * method.
     * <p> This method is invoked by the {@link Files#isHidden isHidden} method.
     *
     * @param path the path to the file to test
     * @return {@code true} if the file is considered hidden
     *
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file.
     */
    @Override
    public final boolean isHidden(final Path path)
        throws IOException
    {
        return getDriver(path).isHidden(path);
    }

    /**
     * Checks the existence, and optionally the accessibility, of a file.
     * <p> This method may be used by the {@link Files#isReadable isReadable},
     * {@link Files#isWritable isWritable} and {@link Files#isExecutable
     * isExecutable} methods to check the accessibility of a file.
     * <p> This method checks the existence of a file and that this Java virtual
     * machine has appropriate privileges that would allow it access the file
     * according to all of access modes specified in the {@code modes} parameter
     * as follows:
     * <table border=1 cellpadding=5 summary="">
     * <tr> <th>Value</th> <th>Description</th> </tr>
     * <tr>
     * <td> {@link AccessMode#READ READ} </td>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to read the file. </td>
     * </tr>
     * <tr>
     * <td> {@link AccessMode#WRITE WRITE} </td>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to write to the file, </td>
     * </tr>
     * <tr>
     * <td> {@link AccessMode#EXECUTE EXECUTE} </td>
     * <td> Checks that the file exists and that the Java virtual machine has
     * permission to {@link Runtime#exec execute} the file. The semantics
     * may differ when checking access to a directory. For example, on UNIX
     * systems, checking for {@code EXECUTE} access checks that the Java
     * virtual machine has permission to search the directory in order to
     * access file or subdirectories. </td>
     * </tr>
     * </table>
     * <p> If the {@code modes} parameter is of length zero, then the existence
     * of the file is checked.
     * <p> This method follows symbolic links if the file referenced by this
     * object is a symbolic link. Depending on the implementation, this method
     * may require to read file permissions, access control lists, or other
     * file attributes in order to check the effective access to the file. To
     * determine the effective access to a file may require access to several
     * attributes and so in some implementations this method may not be atomic
     * with respect to other file system operations.
     *
     * @param path the path to the file to check
     * @param modes The access modes to check; may have zero elements
     * @throws UnsupportedOperationException an implementation is required to
     * support checking for
     * {@code READ}, {@code WRITE}, and {@code EXECUTE} access. This
     * exception is specified to allow for the {@code Access} enum to
     * be extended in future releases.
     * @throws NoSuchFileException if a file does not exist <i>(optional
     * specific exception)</i>
     * @throws AccessDeniedException the requested access would be denied or
     * the access cannot be
     * determined because the Java virtual machine has insufficient
     * privileges or other reasons. <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * is invoked when checking read access to the file or only the
     * existence of the file, the {@link SecurityManager#checkWrite(String)
     * checkWrite} is invoked when checking write access to the file,
     * and {@link SecurityManager#checkExec(String) checkExec} is invoked
     * when checking execute access.
     */
    @Override
    public final void checkAccess(final Path path, final AccessMode... modes)
        throws IOException
    {
        getDriver(path).checkAccess(path, modes);
    }

    /**
     * Returns a file attribute view of a given type. This method works in
     * exactly the manner specified by the {@link Files#getFileAttributeView}
     * method.
     *
     * @param path the path to the file
     * @param type the {@code Class} object corresponding to the file attribute view
     * @param options options indicating how symbolic links are handled
     * @return a file attribute view of the specified type, or {@code null} if
     * the attribute view type is not available
     */
    @Override
    public final <V extends FileAttributeView> V getFileAttributeView(
        final Path path, final Class<V> type, final LinkOption... options)
    {
        return getDriver(path).getFileAttributeView(path, type, options);
    }

    /**
     * Reads a file's attributes as a bulk operation. This method works in
     * exactly the manner specified by the {@link
     * Files#readAttributes(Path, Class, LinkOption[])} method.
     *
     * @param path the path to the file
     * @param type the {@code Class} of the file attributes required
     * to read
     * @param options options indicating how symbolic links are handled
     * @return the file attributes
     *
     * @throws UnsupportedOperationException if an attributes of the given
     * type are not supported
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, its {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file
     */
    @Override
    public final <A extends BasicFileAttributes> A readAttributes(
        final Path path, final Class<A> type, final LinkOption... options)
        throws IOException
    {
        final FileSystemDriver driver = getDriver(path);
        return driver.readAttributes(path, type, options);
    }

    /**
     * Reads a set of file attributes as a bulk operation. This method works in
     * exactly the manner specified by the {@link
     * Files#readAttributes(Path, String, LinkOption[])} method.
     *
     * @param path the path to the file
     * @param attributes the attributes to read
     * @param options options indicating how symbolic links are handled
     * @return a map of the attributes returned; may be empty. The map's keys
     * are the attribute names, its values are the attribute values
     *
     * @throws UnsupportedOperationException if the attribute view is not
     * available
     * @throws IllegalArgumentException if no attributes are specified or an
     * unrecognized attributes is
     * specified
     * @throws IOException If an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, its {@link SecurityManager#checkRead(String) checkRead}
     * method denies read access to the file. If this method is invoked
     * to read security sensitive attributes then the security manager
     * may be invoke to check for additional permissions.
     */
    @Override
    public final Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        return getDriver(path).readAttributes(path, attributes, options);
    }

    /**
     * Sets the value of a file attribute. This method works in exactly the
     * manner specified by the {@link Files#setAttribute} method.
     *
     * @param path the path to the file
     * @param attribute the attribute to set
     * @param value the attribute value
     * @param options options indicating how symbolic links are handled
     * @throws UnsupportedOperationException if the attribute view is not
     * available
     * @throws IllegalArgumentException if the attribute name is not
     * specified, or is not recognized, or
     * the attribute value is of the correct type but has an
     * inappropriate value
     * @throws ClassCastException If the attribute value is not of the
     * expected type or is a
     * collection containing elements that are not of the expected
     * type
     * @throws IOException If an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file. If this method is invoked
     * to set security sensitive attributes then the security manager
     * may be invoked to check for additional permissions.
     */
    @Override
    public final void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        getDriver(path).setAttribute(path, attribute, value, options);
    }

    /**
     * Returns the {@link FileStore} representing the file store where a file
     * is located. This method works in exactly the manner specified by the
     * {@link Files#getFileStore} method.
     *
     * @param path the path to the file
     * @return the file store where the file is stored
     *
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file, and in
     * addition it checks {@link RuntimePermission}<tt>
     * ("getFileStoreAttributes")</tt>
     */
    @Override
    public final FileStore getFileStore(final Path path)
        throws IOException
    {
        // See FileSystemBase: only one file store per filesystem
        return path.getFileSystem().getFileStores().iterator().next();
    }

    /**
     * Convert a set of copy options to a set of open options
     *
     * <p>This method is used in {@link #copy(Path, Path, CopyOption...)} when
     * the filesystems for the source and destination paths are different.</p>
     *
     * <p>The set of options built from this method (if any) will be passed to
     * {@link FileSystemDriver#newOutputStream(Path, OpenOption...)}.</p>
     *
     * <p>The default implementation will refuse both {@link
     * StandardCopyOption#COPY_ATTRIBUTES} and {@link
     * StandardCopyOption#ATOMIC_MOVE}, and will transform {@link
     * StandardCopyOption#REPLACE_EXISTING} into both open options {@link
     * StandardOpenOption#WRITE} and {@link StandardOpenOption#CREATE_NEW}. All
     * other copy options are not supported.</p>
     *
     * @param copyOptions the options to copy (never null)
     * @return the matching set of open options
     * @throws UnsupportedOperationException one specified option is not
     * supported
     */
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

    @Nonnull
    private static FileSystemDriver getDriver(final Path path)
    {
        final FileSystemBase fs = (FileSystemBase) path.getFileSystem();
        if (!fs.isOpen())
            throw new ClosedFileSystemException();
        return fs.getDriver();
    }

    private static final class MoveVisitor
        implements FileVisitor<Path>
    {
        private final FileSystemDriver srcDriver;
        private final Path srcPath;
        private final FileSystemDriver dstDriver;
        private final Path dstPath;

        MoveVisitor(final FileSystemDriver srcDriver, final Path srcPath,
            final FileSystemDriver dstDriver, final Path dstPath)
        {
            this.srcDriver = srcDriver;
            this.srcPath = srcPath;
            this.dstDriver = dstDriver;
            this.dstPath = dstPath;
        }

        /**
         * Invoked for a directory before entries in the directory are visited.
         * <p> If this method returns {@link FileVisitResult#CONTINUE CONTINUE},
         * then entries in the directory are visited. If this method returns
         * {@link
         * FileVisitResult#SKIP_SUBTREE SKIP_SUBTREE} or {@link
         * FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS} then entries in the
         * directory (and any descendants) will not be visited.
         *
         * @param dir a reference to the directory
         * @param attrs the directory's basic attributes
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
            final BasicFileAttributes attrs)
            throws IOException
        {
            final String s = srcPath.relativize(dir).toString();
            final Path toCreate = dstPath.resolve(s);
            dstDriver.createDirectory(toCreate);
            // TODO: set attributes?
            return FileVisitResult.CONTINUE;
        }

        /**
         * Invoked for a file in a directory.
         *
         * @param file a reference to the file
         * @param attrs the file's basic attributes
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attrs)
            throws IOException
        {
            final String s = srcPath.relativize(file).toString();
            final Path toCreate = dstPath.resolve(s);
            try (
                final InputStream in = srcDriver.newInputStream(srcPath);
                final OutputStream out = dstDriver.newOutputStream(toCreate);
            ) {
                final byte[] buf = new byte[BUFSIZE];

                int nrBytes;

                while ((nrBytes = in.read(buf)) != -1)
                    out.write(buf, 0, nrBytes);

                out.flush();
            }
            srcDriver.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Invoked for a file that could not be visited. This method is invoked
         * if the file's attributes could not be read, the file is a directory
         * that could not be opened, and other reasons.
         *
         * @param file a reference to the file
         * @param exc the I/O exception that prevented the file from being
         * visited
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult visitFileFailed(final Path file,
            final IOException exc)
            throws IOException
        {
            throw exc;
        }

        /**
         * Invoked for a directory after entries in the directory, and all of
         * their
         * descendants, have been visited. This method is also invoked when
         * iteration
         * of the directory completes prematurely (by a {@link #visitFile
         * visitFile}
         * method returning {@link FileVisitResult#SKIP_SIBLINGS SKIP_SIBLINGS},
         * or an I/O error when iterating over the directory).
         *
         * @param dir a reference to the directory
         * @param exc {@code null} if the iteration of the directory
         * completes without
         * an error; otherwise the I/O exception that caused the iteration
         * of the directory to complete prematurely
         * @return the visit result
         *
         * @throws IOException if an I/O error occurs
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir,
            final IOException exc)
            throws IOException
        {
            srcDriver.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
