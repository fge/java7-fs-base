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

package com.github.fge.filesystem.fs;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.path.GenericPath;
import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;

import java.io.File;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;

/**
 * Base {@link FileSystem} implementation
 *
 * <p>Limitations:</p>
 *
 * <ul>
 *     <li>only one {@link FileSystem#getRootDirectories() root directory}, only
 *     one {@link FileSystem#getFileStores() file store}.</li>
 * </ul>
 */
public abstract class FileSystemBase
    extends FileSystem
{
    private volatile boolean open = true;

    protected final FileSystemDriver driver;
    protected final FileSystemProvider provider;
    protected final PathElementsFactory factory;
    protected final String separator;

    protected FileSystemBase(final FileSystemDriver driver,
        final FileSystemProvider provider)
    {
        this.driver = driver;
        this.provider = provider;
        factory = driver.getPathElementsFactory();
        separator = factory.getSeparator();
    }


    /**
     * Returns the provider that created this file system.
     *
     * @return The provider that created this file system.
     */
    @Override
    public final FileSystemProvider provider()
    {
        return provider;
    }

    /**
     * Closes this file system.
     * <p> After a file system is closed then all subsequent access to the file
     * system, either by methods defined by this class or on objects associated
     * with this file system, throw {@link ClosedFileSystemException}. If the
     * file system is already closed then invoking this method has no effect.
     * <p> Closing a file system will close all open {@link
     * Channel channels}, {@link DirectoryStream directory-streams},
     * {@link WatchService watch-service}, and other closeable objects
     * associated
     * with this file system. The {@link FileSystems#getDefault default} file
     * system cannot be closed.
     *
     * @throws IOException If an I/O error occurs
     * @throws UnsupportedOperationException Thrown in the case of the
     * default file system
     */
    @Override
    public final void close()
        throws IOException
    {
        open = false;
        driver.close();
    }

    /**
     * Tells whether or not this file system is open.
     * <p> File systems created by the default provider are always open.
     *
     * @return {@code true} if, and only if, this file system is open
     */
    @Override
    public final boolean isOpen()
    {
        return open;
    }

    /**
     * Tells whether or not this file system allows only read-only access to
     * its file stores.
     *
     * @return {@code true} if, and only if, this file system provides
     * read-only access
     */
    @Override
    public final boolean isReadOnly()
    {
        return driver.getFileStore().isReadOnly();
    }

    /**
     * Returns the name separator, represented as a string.
     * <p> The name separator is used to separate names in a path string. An
     * implementation may support multiple name separators in which case this
     * method returns an implementation specific <em>default</em> name
     * separator.
     * This separator is used when creating path strings by invoking the {@link
     * Path#toString() toString()} method.
     * <p> In the case of the default provider, this method returns the same
     * separator as {@link File#separator}.
     *
     * @return The name separator
     */
    @Override
    public final String getSeparator()
    {
        return separator;
    }

    /**
     * Returns an object to iterate over the paths of the root directories.
     * <p> A file system provides access to a file store that may be composed
     * of a number of distinct file hierarchies, each with its own top-level
     * root directory. Unless denied by the security manager, each element in
     * the returned iterator corresponds to the root directory of a distinct
     * file hierarchy. The order of the elements is not defined. The file
     * hierarchies may change during the lifetime of the Java virtual machine.
     * For example, in some implementations, the insertion of removable media
     * may result in the creation of a new file hierarchy with its own
     * top-level directory.
     * <p> When a security manager is installed, it is invoked to check access
     * to the each root directory. If denied, the root directory is not returned
     * by the iterator. In the case of the default provider, the {@link
     * SecurityManager#checkRead(String)} method is invoked to check read access
     * to each root directory. It is system dependent if the permission checks
     * are done when the iterator is obtained or during iteration.
     *
     * @return An object to iterate over the root directories
     */
    @Override
    public final Iterable<Path> getRootDirectories()
    {
        return Collections.<Path>singletonList(
            new GenericPath(this, factory, driver.getRoot())
        );
    }

    /**
     * Returns an object to iterate over the underlying file stores.
     * <p> The elements of the returned iterator are the {@link
     * FileStore FileStores} for this file system. The order of the elements is
     * not defined and the file stores may change during the lifetime of the
     * Java virtual machine. When an I/O error occurs, perhaps because a file
     * store is not accessible, then it is not returned by the iterator.
     * <p> In the case of the default provider, and a security manager is
     * installed, the security manager is invoked to check {@link
     * RuntimePermission}<tt>("getFileStoreAttributes")</tt>. If denied, then
     * no file stores are returned by the iterator. In addition, the security
     * manager's {@link SecurityManager#checkRead(String)} method is invoked to
     * check read access to the file store's <em>top-most</em> directory. If
     * denied, the file store is not returned by the iterator. It is system
     * dependent if the permission checks are done when the iterator is obtained
     * or during iteration.
     * <p> <b>Usage Example:</b>
     * Suppose we want to print the space usage for all file stores:
     * <pre>
     *     for (FileStore store: FileSystems.getDefault().getFileStores()) {
     *         long total = store.getTotalSpace() / 1024;
     *         long used = (store.getTotalSpace() - store.getUnallocatedSpace
     *         ()) / 1024;
     *         long avail = store.getUsableSpace() / 1024;
     *         System.out.format("%-20s %12d %12d %12d%n", store, total,
     *         used, avail);
     *     }
     * </pre>
     *
     * @return An object to iterate over the backing file stores
     */
    @Override
    public final Iterable<FileStore> getFileStores()
    {
        return Collections.singletonList(driver.getFileStore());
    }

    /**
     * Converts a path string, or a sequence of strings that when joined form
     * a path string, to a {@code Path}. If {@code more} does not specify any
     * elements then the value of the {@code first} parameter is the path string
     * to convert. If {@code more} specifies one or more elements then each
     * non-empty string, including {@code first}, is considered to be a sequence
     * of name elements (see {@link Path}) and is joined to form a path string.
     * The details as to how the Strings are joined is provider specific but
     * typically they will be joined using the {@link #getSeparator
     * name-separator} as the separator. For example, if the name separator is
     * "{@code /}" and {@code getPath("/foo","bar","gus")} is invoked, then the
     * path string {@code "/foo/bar/gus"} is converted to a {@code Path}.
     * A {@code Path} representing an empty path is returned if {@code first}
     * is the empty string and {@code more} does not contain any non-empty
     * strings.
     * <p> The parsing and conversion to a path object is inherently
     * implementation dependent. In the simplest case, the path string is
     * rejected,
     * and {@link InvalidPathException} thrown, if the path string contains
     * characters that cannot be converted to characters that are <em>legal</em>
     * to the file store. For example, on UNIX systems, the NUL (&#92;u0000)
     * character is not allowed to be present in a path. An implementation may
     * choose to reject path strings that contain names that are longer than
     * those
     * allowed by any file store, and where an implementation supports a complex
     * path syntax, it may choose to reject path strings that are <em>badly
     * formed</em>.
     * <p> In the case of the default provider, path strings are parsed based
     * on the definition of paths at the platform or virtual file system level.
     * For example, an operating system may not allow specific characters to be
     * present in a file name, but a specific underlying file store may impose
     * different or additional restrictions on the set of legal
     * characters.
     * <p> This method throws {@link InvalidPathException} when the path string
     * cannot be converted to a path. Where possible, and where applicable,
     * the exception is created with an {@link InvalidPathException#getIndex
     * index} value indicating the first position in the {@code path} parameter
     * that caused the path string to be rejected.
     *
     * @param first the path string or initial part of the path string
     * @param more additional strings to be joined to form the path string
     * @return the resulting {@code Path}
     *
     * @throws InvalidPathException If the path string cannot be converted
     */
    @SuppressWarnings("OverloadedVarargsMethod")
    @Override
    public final Path getPath(final String first, final String... more)
    {
        if (more.length == 0)
            return new GenericPath(this, factory,
                factory.toPathElements(first));

        final StringBuilder sb = new StringBuilder(first);

        for (final String s: more)
            if (!s.isEmpty())
                sb.append(separator).append(s);

        final PathElements elements = factory.toPathElements(sb.toString());
        return new GenericPath(this, factory, elements);
    }
}
