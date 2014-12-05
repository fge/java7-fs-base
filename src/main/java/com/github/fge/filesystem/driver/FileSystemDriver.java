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
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.Nonnull;
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
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public interface FileSystemDriver
    extends Closeable
{
    @Nonnull
    URI getUri();

    @Nonnull
    PathElementsFactory getPathElementsFactory();

    @Nonnull
    PathElements getRoot();

    @Nonnull
    FileStore getFileStore();

    @Nonnull
    Set<String> getSupportedFileAttributeViews();

    @Nonnull
    PathMatcherProvider getPathMatcherProvider();

    @Nonnull
    UserPrincipalLookupService getUserPrincipalLookupService();

    @Nonnull
    WatchService newWatchService();

    @Nonnull
    InputStream newInputStream(Path path, OpenOption... options)
        throws IOException;

    @Nonnull
    OutputStream newOutputStream(Path path, OpenOption... options)
        throws IOException;

    @Nonnull
    SeekableByteChannel newByteChannel(Path path,
        Set<? extends OpenOption> options, FileAttribute<?>... attrs)
        throws IOException;

    @Nonnull
    DirectoryStream<Path> newDirectoryStream(Path dir,
        DirectoryStream.Filter<? super Path> filter)
        throws IOException;

    void createDirectory(Path dir, FileAttribute<?>... attrs)
        throws IOException;

    void delete(Path path)
        throws IOException;

    void copy(Path source, Path target, CopyOption... options)
        throws IOException;

    void move(Path source, Path target, CopyOption... options)
        throws IOException;

    boolean isSameFile(Path path, Path path2)
        throws IOException;

    boolean isHidden(Path path)
        throws IOException;

    void checkAccess(Path path, AccessMode... modes);

    <V extends FileAttributeView> V getFileAttributeView(Path path,
        Class<V> type, LinkOption... options);

    <A extends BasicFileAttributes, V extends FileAttributeView> A readAttributes(
        Path path, Class<A> type, LinkOption... options)
        throws IOException;

     Map<String, Object> readAttributes(Path path, String attributes,
        LinkOption... options)
        throws IOException;

    void setAttribute(Path path, String attribute, Object value,
        LinkOption... options);
}
