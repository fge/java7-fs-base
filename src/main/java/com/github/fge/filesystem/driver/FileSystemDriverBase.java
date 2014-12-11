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
import com.github.fge.filesystem.path.WatchQueue;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileStore;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * A {@link FileSystemDriver} with some reasonable defaults
 *
 * <p>The (overridable) defaults are:</p>
 *
 * <ul>
 *     <li>only {@link BasicFileAttributeView basic} file attributes are
 *     supported;</li>
 *     <li>no support for {@link UserPrincipalLookupService}s or {@link
 *     WatchService}s (both relevant methods throw an {@link
 *     UnsupportedOperationException});</li>
 *     <li>no support for {@link SeekableByteChannel}s;</li>
 *     <li>{@link #isSameFile(Path, Path)} returns true if and only if both
 *     arguments are {@link Object#equals(Object) equal}.</li>
 * </ul>
 *
 * @see UnixLikeFileSystemDriverBase
 */
@SuppressWarnings("OverloadedVarargsMethod")
@ParametersAreNonnullByDefault
public abstract class FileSystemDriverBase
    implements FileSystemDriver
{
    private final URI uri;
    protected final PathElementsFactory pathElementsFactory;
    private final FileStore fileStore;
    private final PathMatcherProvider pathMatcherProvider;

    protected FileSystemDriverBase(final URI uri,
        final PathElementsFactory pathElementsFactory,
        final FileStore fileStore,
        final PathMatcherProvider pathMatcherProvider)
    {
        this.uri = Objects.requireNonNull(uri);
        this.pathElementsFactory = Objects.requireNonNull(pathElementsFactory);
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
    	Path directory = Paths.get(uri);
    	WatchService watcher = null;
		try {
			watcher = directory.getFileSystem().newWatchService();
	    	WatchQueue queue = new WatchQueue(watcher);
	        Thread th = new Thread(queue, "WatchQueue");
	        th.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return watcher;
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
        return path.equals(path2);
    }
}
