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
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public abstract class FileSystemDriverBase
    implements FileSystemDriver
{
    private final URI uri;
    private final PathElementsFactory pathElementsFactory;
    private final PathElements root;
    private final FileStore fileStore;
    private final PathMatcherProvider pathMatcherProvider;

    protected FileSystemDriverBase(final URI uri,
        final PathElementsFactory pathElementsFactory, final PathElements root,
        final FileStore fileStore,
        final PathMatcherProvider pathMatcherProvider)
    {
        this.uri = Objects.requireNonNull(uri);
        this.pathElementsFactory = Objects.requireNonNull(pathElementsFactory);
        this.root = Objects.requireNonNull(root);
        this.fileStore = Objects.requireNonNull(fileStore);
        this.pathMatcherProvider = Objects.requireNonNull(pathMatcherProvider);
    }

    @Nonnull
    @Override
    public final URI getUri()
    {
        return uri;
    }

    @Nonnull
    @Override
    public final PathElementsFactory getPathElementsFactory()
    {
        return pathElementsFactory;
    }

    @Nonnull
    @Override
    public final PathElements getRoot()
    {
        return root;
    }

    @Nonnull
    @Override
    public final FileStore getFileStore()
    {
        return fileStore;
    }

    @Nonnull
    @Override
    public final PathMatcherProvider getPathMatcherProvider()
    {
        return pathMatcherProvider;
    }

    @SuppressWarnings("DesignForExtension")
    @Nonnull
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("DesignForExtension")
    @Nonnull
    @Override
    public WatchService newWatchService()
    {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("DesignForExtension")
    @Nonnull
    @Override
    public SeekableByteChannel newByteChannel(final Path path,
        final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        return path.equals(path2);
    }
}
