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

import com.github.fge.filesystem.attributes.FileAttributesFactory;
import com.github.fge.filesystem.attributes.provider.FileAttributesProvider;
import com.github.fge.filesystem.exceptions.UncaughtIOException;
import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * A {@link FileSystemDriver} with some reasonable defaults
 *
 * <p>The (overridable) defaults are:</p>
 *
 * <ul>
 *     <li>no support for {@link UserPrincipalLookupService}s or {@link
 *     WatchService}s (both relevant methods throw an {@link
 *     UnsupportedOperationException});</li>
 *     <li>no support for {@link SeekableByteChannel}s;</li>
 *     <li>{@link #isSameFile(Path, Path)} returns true if and only if both
 *     their absolute versions are {@link Object#equals(Object) equal}.</li>
 * </ul>
 *
 * @see UnixLikeFileSystemDriverBase
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public abstract class FileSystemDriverBase
    implements FileSystemDriver
{
    private static final Pattern COMMA = Pattern.compile(",");

    protected final PathElementsFactory pathElementsFactory;
    private final FileStore fileStore;
    private final PathMatcherProvider pathMatcherProvider;
    private final FileAttributesFactory attributesFactory;

    protected FileSystemDriverBase(
        final PathElementsFactory pathElementsFactory,
        final FileStore fileStore,
        final PathMatcherProvider pathMatcherProvider,
        final FileAttributesFactory attributesFactory)
    {
        this.attributesFactory = attributesFactory;
        this.pathElementsFactory = Objects.requireNonNull(pathElementsFactory);
        this.fileStore = Objects.requireNonNull(fileStore);
        this.pathMatcherProvider = Objects.requireNonNull(pathMatcherProvider);
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
        return pathElementsFactory.getRootPathElements();
    }

    @Nonnull
    @Override
    public final FileStore getFileStore()
    {
        return fileStore;
    }

    @SuppressWarnings("DesignForExtension")
    @Nonnull
    @Override
    public Set<String> getSupportedFileAttributeViews()
    {
        return Collections.singleton("basic");
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

    @SuppressWarnings("DesignForExtension")
    @Override
    public boolean isSameFile(final Path path, final Path path2)
        throws IOException
    {
        return path.toAbsolutePath().equals(path2.toAbsolutePath());
    }

    @Override
    public final void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        final int index = attribute.indexOf(':');
        final String type;
        final String name;

        if (index == -1) {
            type = "basic";
            name = attribute;
        } else {
            type = attribute.substring(0, index);
            name = attribute.substring(index + 1);
        }

        final Object metadata = getPathMetadata(path);

        final FileAttributesProvider provider
            = attributesFactory.getProvider(type, metadata);

        if (provider == null)
            throw new UnsupportedOperationException();

        provider.setAttributeByName(name, value);
    }

    @Override
    public final Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        final int index = attributes.indexOf(':');

        final String type;
        final String names;

        if (index == -1) {
            type = "basic";
            names = attributes;
        } else {
            type = attributes.substring(0, index);
            names = attributes.substring(index + 1);
        }

        final Object metadata = getPathMetadata(path.toRealPath(options));

        final FileAttributesProvider provider
            = attributesFactory.getProvider(type, metadata);

        if (provider == null)
            throw new UnsupportedOperationException();

        if ("*".equals(names))
            return provider.getAllAttributes();

        final Map<String, Object> map = new HashMap<>();

        for (final String name: COMMA.split(names))
            map.put(name, provider.getAttributeByName(name));

        return Collections.unmodifiableMap(map);
    }

    @Override
    public final <A extends BasicFileAttributes> A readAttributes(
        final Path path, final Class<A> type, final LinkOption... options)
        throws IOException
    {
        final Object metadata = getPathMetadata(path.toRealPath(options));

        return attributesFactory.getFileAttributes(type, metadata);
    }

    @Nullable
    @Override
    public final <V extends FileAttributeView> V getFileAttributeView(
        final Path path, final Class<V> type, final LinkOption... options)
    {
        final Object metadata;
        try {
            metadata = getPathMetadata(path.toRealPath(options));
            return attributesFactory.getFileAttributeView(type, metadata);
        } catch (IOException e) {
            throw new UncaughtIOException("Unhandled I/O exception", e);
        }
    }
}
