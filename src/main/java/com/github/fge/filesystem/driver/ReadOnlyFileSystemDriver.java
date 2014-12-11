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

package com.github.fge.filesystem.driver;

import com.github.fge.filesystem.filestore.FileStoreBase;
import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link FileSystemDriver} composition implementation over another driver
 * for read only filesystems
 *
 * <p>This class will delegate all read only operations to the underlying driver
 * and will make all write operations throw a {@link
 * ReadOnlyFileSystemException}.</p>
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public final class ReadOnlyFileSystemDriver
    implements FileSystemDriver
{
    private static final Set<OpenOption> WRITE_OPTIONS;

    static {
        final Set<OpenOption> set = new HashSet<>();

        set.add(StandardOpenOption.CREATE_NEW);
        set.add(StandardOpenOption.CREATE);
        set.add(StandardOpenOption.WRITE);
        set.add(StandardOpenOption.APPEND);
        set.add(StandardOpenOption.DELETE_ON_CLOSE);
        set.add(StandardOpenOption.TRUNCATE_EXISTING);

        WRITE_OPTIONS = Collections.unmodifiableSet(set);
    }


    private final FileSystemDriver delegate;

    private ReadOnlyFileSystemDriver(final FileSystemDriver delegate)
    {
        this.delegate = Objects.requireNonNull(delegate);
    }
    
    /**
     * Creates a new {@link FileSystemDriver} if fs is not the instance of, otherwise returns the same.
     * @param fs the FileSystemDriver
     * @return a new or already created FileSystemDriver
     */
    public static FileSystemDriver of(@Nonnull final FileSystemDriver fs) { 
    	Objects.requireNonNull(fs); 
    	return fs instanceof ReadOnlyFileSystemDriver ? fs : new ReadOnlyFileSystemDriver(fs);
    }

    /**
     * Obtain a new {@link OutputStream} from a path for this filesystem
     *
     * @param path the path
     * @param options the set of open options
     * @return a new output stream
     *
     * @throws IOException filesystem level error, or plain I/O error
     * @see FileSystemProvider#newOutputStream(Path, OpenOption...)
     */
    @Nonnull
    @Override
    public OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Obtain a new {@link SeekableByteChannel} from a path for this filesystem
     * <p>Note that a {@code SeekableByteChannel} supports both reads and
     * writes.</p>
     *
     * @param path the path
     * @param options the set of open options
     * @param attrs the attributes to create the file with (if it does not
     * exist)
     * @return a channel
     *
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#newByteChannel(Path, Set, FileAttribute[])
     */
    @Nonnull
    @Override
    public SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        final Set<? extends OpenOption> set = new HashSet<>(WRITE_OPTIONS);
        set.retainAll(options);
        if (!set.isEmpty())
            throw new ReadOnlyFileSystemException();
        return delegate.newByteChannel(path, options, attrs);
    }

    /**
     * Create a new directory from a path on this filesystem
     *
     * @param dir the directory to create
     * @param attrs the attributes with which the directory should be created
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#newDirectoryStream(Path, DirectoryStream.Filter)
     */
    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Delete a file, or empty directory, matching a path on this filesystem
     *
     * @param path the victim
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#delete(Path)
     */
    @Override
    public void delete(final Path path)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Copy a file, or empty directory, from one path to another on this
     * filesystem
     *
     * @param source the source path
     * @param target the target path
     * @param options the copy options
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#copy(Path, Path, CopyOption...)
     */
    @Override
    public void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Move a file, or empty directory, from one path to another on this
     * filesystem
     *
     * @param source the source path
     * @param target the target path
     * @param options the copy options
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#move(Path, Path, CopyOption...)
     */
    @Override
    public void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Set an attribute for a path on this filesystem
     *
     * @param path the victim
     * @param attribute the name of the attribute to set
     * @param value the value to set
     * @param options the link options
     * @throws IOException filesystem level error, or a plain I/O error
     * @throws IllegalArgumentException malformed attribute, or the specified
     * attribute does not exist
     * @throws UnsupportedOperationException the attribute to set is not
     * supported by this filesystem
     * @throws ClassCastException attribute value is of the wrong class for the
     * specified attribute
     * @see Files#setAttribute(Path, String, Object, LinkOption...)
     * @see FileSystemProvider#setAttribute(Path, String, Object, LinkOption...)
     */
    @Override
    public void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    /**
     * Get the URI associated with this filesystem
     *
     * @return a URI (always absolute and hierarchical)
     */
    @Override
    @Nonnull
    public URI getUri()
    {
        return delegate.getUri();
    }

    /**
     * Get the {@link PathElementsFactory} associated with this filesystem
     *
     * @return a path elements factory
     */
    @Override
    @Nonnull
    public PathElementsFactory getPathElementsFactory()
    {
        return delegate.getPathElementsFactory();
    }

    /**
     * Get the root path elements for this filesystem
     *
     * <p>Typically, for Unix-like systems, this will be {@code /}.</p>
     *
     * @return a {@link PathElements} representing the root of the hierarchy
     *
     * @see FileSystem#getRootDirectories()
     */
    @Override
    @Nonnull
    public PathElements getRoot()
    {
        return delegate.getRoot();
    }

    /**
     * Get the {@link FileStore} associated with this filesystem
     *
     * @return a {@link FileStore}
     *
     * @see FileStoreBase
     */
    @Override
    @Nonnull
    public FileStore getFileStore()
    {
        return delegate.getFileStore();
    }

    /**
     * Get a non-modifiable set of file attribute views supported by this
     * filesystem
     *
     * <p>This set (and therefore all implemented filesystems) must at least
     * contain {@code "basic"}.</p>
     *
     * @return an immutable set
     *
     * @see FileSystem#supportedFileAttributeViews()
     */
    @Override
    @Nonnull
    public Set<String> getSupportedFileAttributeViews()
    {
        return delegate.getSupportedFileAttributeViews();
    }

    /**
     * Get a {@link PathMatcher} provider for this filesystem
     *
     * @return a path matcher provider
     *
     * @see FileSystem#getPathMatcher(String)
     * @see com.github.fge.filesystem.path.matchers
     */
    @Override
    @Nonnull
    public PathMatcherProvider getPathMatcherProvider()
    {
        return delegate.getPathMatcherProvider();
    }

    /**
     * Get a user/group lookup service for this filesystem
     *
     * @return a user/group lookup service
     *
     * @see FileSystem#getUserPrincipalLookupService()
     */
    @Override
    @Nonnull
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return delegate.getUserPrincipalLookupService();
    }

    /**
     * Get a file watch service for this filesystem
     *
     * @return a watch service
     *
     * @see FileSystem#newWatchService()
     */
    @Override
    @Nonnull
    public WatchService newWatchService()
    {
    	//TODO: No idea if I should implement watch service for this one?
        return delegate.newWatchService();
    }

    /**
     * Obtain a new {@link InputStream} from a path for this filesystem
     *
     * @param path the path
     * @param options the set of open options
     * @return a new input stream
     * @throws IOException filesystem level error, or plain I/O error
     *
     * @see FileSystemProvider#newInputStream(Path, OpenOption...)
     */
    @Override
    @Nonnull
    public InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        return delegate.newInputStream(path, options);
    }

    /**
     * Create a new directory stream from a path for this filesystem
     *
     * @param dir the directory
     * @param filter a directory entry filter
     * @return a directory stream
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#newDirectoryStream(Path, DirectoryStream.Filter)
     */
    @Override
    @Nonnull
    public DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        return delegate.newDirectoryStream(dir, filter);
    }

    /**
     * Tell whether two paths actually refer to the same resource on this
     * filesystem
     *
     * <p>Note that this DOES NOT apply to symbolic links, if the filesystem
     * supports them; that is, if {@code path} is a symlink to {@code path2},
     * they are <em>not</em> the same file. Also, in spite of the method name,
     * this method can be called on paths which are not regular files but
     * directories, symlinks or others.</p>
     *
     * <p>Two paths which are {@link Object#equals(Object) equal} are always the
     * same.</p>
     *
     * @param path the first path
     * @param path2 the second path
     * @return true if and only if both path
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#isSameFile(Path, Path)
     */
    @Override
    public boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        return delegate.isSameFile(path, path2);
    }

    /**
     * Tell whether a path is to be considered hidden by this filesystem
     *
     * <p>Typically, on Unix systems, it means the last name element of the path
     * starts with a dot ({@code "."}).</p>
     *
     * @param path the path to test
     * @return true if this path is considered hidden
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#isHidden(Path)
     */
    @Override
    public boolean isHidden(final Path path)
        throws IOException
    {
        return delegate.isHidden(path);
    }

    /**
     * Check access modes for a path on this filesystem
     *
     * <p>If no modes are provided to check for, this simply checks for the
     * existence of the path.</p>
     *
     * @param path the path to check
     * @param modes the modes to check for, if any
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#checkAccess(Path, AccessMode...)
     */
    @Override
    public void checkAccess(final Path path, final AccessMode... modes)
        throws IOException
    {
        delegate.checkAccess(path, modes);
    }

    /**
     * Read an attribute view for a given path on this filesystem
     *
     * @param path the path to read attributes from
     * @param type the class of attribute view to return
     * @param options the link options
     * @return the attributes view; {@code null} if this view is not supported
     *
     * @see FileSystemProvider#getFileAttributeView(Path, Class, LinkOption...)
     */
    @Override
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        return delegate.getFileAttributeView(path, type, options);
    }

    /**
     * Read attributes from a path on this filesystem
     *
     * @param path the path to read attributes from
     * @param type the class of attributes to read
     * @param options the link options
     * @return the attributes
     * @throws IOException filesystem level error, or a plain I/O error
     * @throws UnsupportedOperationException attribute type not supported
     *
     * @see FileSystemProvider#readAttributes(Path, Class, LinkOption...)
     */
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, type, options);
    }

    /**
     * Read a list of attributes from a path on this filesystem
     *
     * @param path the path to read attributes from
     * @param attributes the list of attributes to read
     * @param options the link options
     * @return the relevant attributes as a map
     * @throws IOException filesystem level error, or a plain I/O error
     * @throws IllegalArgumentException malformed attributes string; or a
     * specified attribute does not exist
     * @throws UnsupportedOperationException one or more attribute(s) is/are not
     * supported
     *
     * @see Files#readAttributes(Path, String, LinkOption...)
     * @see FileSystemProvider#readAttributes(Path, String, LinkOption...)
     */
    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, attributes, options);
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close()
        throws IOException
    {
        delegate.close();
    }
}
