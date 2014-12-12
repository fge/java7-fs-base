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
import com.github.fge.filesystem.fs.GenericFileSystem;
import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;
import com.github.fge.filesystem.provider.FileSystemProviderBase;
import com.github.fge.filesystem.provider.FileSystemRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Closeable;
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
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;

/**
 * The core filesystem class
 *
 * <p>This is the interface you have to implement in order to provide the guts
 * of a {@link FileSystem} implementation. Rather than implementing this class
 * directly, you want to extends {@link FileSystemDriverBase} instead, or even
 * {@link UnixLikeFileSystemDriverBase}.</p>
 *
 * <p>Due to the design of this project, this is the core class for all
 * filesystem interactions. All methods dealing with I/O are implemented here
 * and not in {@link FileSystemProvider}; it also contains methods specifically
 * used by {@link FileSystem}.</p>
 *
 * @see GenericFileSystem
 * @see FileSystemProviderBase
 * @see FileSystemRepository
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public interface FileSystemDriver
    extends Closeable
{
    /**
     * Get the URI associated with this filesystem
     *
     * @return a URI (always absolute and hierarchical)
     */
    @Nonnull
    URI getUri();

    /**
     * Get the {@link PathElementsFactory} associated with this filesystem
     *
     * @return a path elements factory
     */
    @Nonnull
    PathElementsFactory getPathElementsFactory();

    /**
     * Get the root path elements for this filesystem
     *
     * <p>Typically, for Unix-like systems, this will be {@code /}.</p>
     *
     * @return a {@link PathElements} representing the root of the hierarchy
     *
     * @see FileSystem#getRootDirectories()
     */
    @Nonnull
    PathElements getRoot();

    /**
     * Get the {@link FileStore} associated with this filesystem
     *
     * @return a {@link FileStore}
     *
     * @see FileStoreBase
     */
    @Nonnull
    FileStore getFileStore();

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
    @Nonnull
    Set<String> getSupportedFileAttributeViews();

    /**
     * Get a {@link PathMatcher} provider for this filesystem
     *
     * @return a path matcher provider
     *
     * @see FileSystem#getPathMatcher(String)
     * @see com.github.fge.filesystem.path.matchers
     */
    @Nonnull
    PathMatcherProvider getPathMatcherProvider();

    /**
     * Get a user/group lookup service for this filesystem
     *
     * @return a user/group lookup service
     *
     * @see FileSystem#getUserPrincipalLookupService()
     */
    @Nonnull
    UserPrincipalLookupService getUserPrincipalLookupService();

    /**
     * Get a file watch service for this filesystem
     *
     * @return a watch service
     *
     * @see FileSystem#newWatchService()
     */
    @Nonnull
    WatchService newWatchService();

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
    @Nonnull
    InputStream newInputStream(Path path, OpenOption... options)
        throws IOException;

    /**
     * Obtain a new {@link OutputStream} from a path for this filesystem
     *
     * @param path the path
     * @param options the set of open options
     * @return a new output stream
     * @throws IOException filesystem level error, or plain I/O error
     *
     * @see FileSystemProvider#newOutputStream(Path, OpenOption...)
     */
    @Nonnull
    OutputStream newOutputStream(Path path, OpenOption... options)
        throws IOException;

    /**
     * Obtain a new {@link SeekableByteChannel} from a path for this filesystem
     *
     * <p>Note that a {@code SeekableByteChannel} supports both reads and
     * writes.</p>
     *
     * @param path the path
     * @param options the set of open options
     * @param attrs the attributes to create the file with (if it does not
     * exist)
     * @return a channel
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#newByteChannel(Path, Set, FileAttribute[])
     */
    @Nonnull
    SeekableByteChannel newByteChannel(Path path,
        Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws IOException;

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
    @Nonnull
    DirectoryStream<Path> newDirectoryStream(Path dir,
        DirectoryStream.Filter<? super Path> filter)
        throws IOException;

    /**
     * Create a new directory from a path on this filesystem
     *
     * @param dir the directory to create
     * @param attrs the attributes with which the directory should be created
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#newDirectoryStream(Path, DirectoryStream.Filter)
     */
    void createDirectory(Path dir, FileAttribute<?>... attrs)
        throws IOException;

    /**
     * Delete a file, or empty directory, matching a path on this filesystem
     *
     * @param path the victim
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#delete(Path)
     */
    void delete(Path path)
        throws IOException;

    /**
     * Copy a file, or empty directory, from one path to another on this
     * filesystem
     *
     * @param source the source path
     * @param target the target path
     * @param options the copy options
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#copy(Path, Path, CopyOption...)
     */
    void copy(Path source, Path target, CopyOption... options)
        throws IOException;

    /**
     * Move a file, or empty directory, from one path to another on this
     * filesystem
     *
     * @param source the source path
     * @param target the target path
     * @param options the copy options
     * @throws IOException filesystem level error, or a plain I/O error
     *
     * @see FileSystemProvider#move(Path, Path, CopyOption...)
     */
    void move(Path source, Path target, CopyOption... options)
        throws IOException;

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
    boolean isSameFile(Path path, Path path2)
        throws IOException;

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
    boolean isHidden(Path path)
        throws IOException;

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
    void checkAccess(Path path, AccessMode... modes)
        throws IOException;

    /**
     * Read an attribute view for a given path on this filesystem
     *
     * @param path the path to read attributes from
     * @param type the class of attribute view to return
     * @param options the link options
     * @param <V> type parameter of the attribute view class
     * @return the attributes view; {@code null} if this view is not supported
     *
     * @see FileSystemProvider#getFileAttributeView(Path, Class, LinkOption...)
     */
    @Nullable
    <V extends FileAttributeView> V getFileAttributeView(Path path,
        Class<V> type, LinkOption... options);

    /**
     * Read attributes from a path on this filesystem
     *
     * @param path the path to read attributes from
     * @param type the class of attributes to read
     * @param options the link options
     * @param <A> parameter type for the attributs class
     * @return the attributes
     * @throws IOException filesystem level error, or a plain I/O error
     * @throws UnsupportedOperationException attribute type not supported
     *
     * @see FileSystemProvider#readAttributes(Path, Class, LinkOption...)
     */
    <A extends BasicFileAttributes> A readAttributes(
        Path path, Class<A> type, LinkOption... options)
        throws IOException;

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
     Map<String, Object> readAttributes(Path path, String attributes,
        LinkOption... options)
        throws IOException;

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
     *
     * @see Files#setAttribute(Path, String, Object, LinkOption...)
     * @see FileSystemProvider#setAttribute(Path, String, Object, LinkOption...)
     */
    void setAttribute(Path path, String attribute, Object value,
        LinkOption... options)
        throws IOException;

    @Nonnull
    Object getPathMetadata(Path path)
        throws IOException;
}
