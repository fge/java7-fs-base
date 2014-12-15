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

import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
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
// TODO: not plugged in for now
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

    @Nonnull
    public static FileSystemDriver wrap(@Nonnull final FileSystemDriver driver)
    {
        Objects.requireNonNull(driver);
        return driver instanceof ReadOnlyFileSystemDriver ? driver
            : new ReadOnlyFileSystemDriver(driver);
    }

    private ReadOnlyFileSystemDriver(final FileSystemDriver delegate)
    {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Nonnull
    @Override
    public OutputStream newOutputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

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

    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void delete(final Path path)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void copy(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void move(final Path source, final Path target,
        final CopyOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    public void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        throw new ReadOnlyFileSystemException();
    }

    @Override
    @Nonnull
    public PathElementsFactory getPathElementsFactory()
    {
        return delegate.getPathElementsFactory();
    }

    @Override
    @Nonnull
    public PathElements getRoot()
    {
        return delegate.getRoot();
    }

    @Override
    @Nonnull
    public FileStore getFileStore()
    {
        return delegate.getFileStore();
    }

    @Override
    @Nonnull
    public Set<String> getSupportedFileAttributeViews()
    {
        return delegate.getSupportedFileAttributeViews();
    }

    @Override
    @Nonnull
    public PathMatcherFactory getPathMatcherFactory()
    {
        return delegate.getPathMatcherFactory();
    }

    @Override
    @Nonnull
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return delegate.getUserPrincipalLookupService();
    }

    @Override
    @Nonnull
    public WatchService newWatchService()
    {
        return delegate.newWatchService();
    }

    @Override
    @Nonnull
    public InputStream newInputStream(final Path path,
        final OpenOption... options)
        throws IOException
    {
        return delegate.newInputStream(path, options);
    }

    @Override
    @Nonnull
    public DirectoryStream<Path> newDirectoryStream(final Path dir,
        final DirectoryStream.Filter<? super Path> filter)
        throws IOException
    {
        return delegate.newDirectoryStream(dir, filter);
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        return delegate.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(final Path path)
        throws IOException
    {
        return delegate.isHidden(path);
    }

    @Override
    public void checkAccess(final Path path, final AccessMode... modes)
        throws IOException
    {
        delegate.checkAccess(path, modes);
    }

    @Override
    @Nullable
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        return delegate.getFileAttributeView(path, type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        return delegate.readAttributes(path, attributes, options);
    }

    @Nonnull
    @Override
    public Object getPathMetadata(final Path path)
        throws IOException
    {
        return delegate.getPathMetadata(path);
    }

    @Override
    public void close()
        throws IOException
    {
        delegate.close();
    }
}
