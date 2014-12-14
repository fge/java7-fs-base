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
import com.github.fge.filesystem.path.UnixPathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.spi.FileSystemProvider;

/**
 * A base abstract implementation of a {@link FileSystemDriver} for Unix-like
 * filesystems
 *
 * <p>In addition to the defaults defined in {@link FileSystemDriverBase}, this
 * abstract class defaults to a {@link UnixPathElementsFactory} for building
 * paths and a default {@link PathMatcherProvider} to provide {@link
 * PathMatcher} instances -- therefore bringing support for both {@code "glob"}
 * and {@code "regex"} path matchers. It also considers that all paths whose
 * last name element begin with a dot are hidden (overridable).</p>
 */
@ParametersAreNonnullByDefault
public abstract class UnixLikeFileSystemDriverBase
    extends FileSystemDriverBase
{
    protected UnixLikeFileSystemDriverBase(final URI uri,
        final FileStore fileStore,
        final FileAttributesFactory attributesFactory)
    {
        super(uri, new UnixPathElementsFactory(), fileStore,
            new PathMatcherProvider(), attributesFactory);
    }

    /**
     * Tell whether a path is to be considered hidden by this filesystem
     * <p>Typically, on Unix systems, it means the last name element of the path
     * starts with a dot ({@code "."}).</p>
     *
     * @param path the path to test
     * @return true if this path is considered hidden
     *
     * @throws IOException filesystem level error, or a plain I/O error
     * @see FileSystemProvider#isHidden(Path)
     */
    @SuppressWarnings("DesignForExtension")
    @Override
    public boolean isHidden(final Path path)
        throws IOException
    {
        final Path filename = path.getFileName();
        return filename != null && filename.toString().startsWith(".");
    }
}
