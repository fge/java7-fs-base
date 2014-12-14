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

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

/**
 * File system repository
 *
 * <p>A {@link FileSystemProvider} delegates all filesystem creation/unregister
 * operations to an implementation of this interface.</p>
 *
 * <p>Unless otherwise noted, all methods of this interface do not support null
 * arguments; calling one method with a null argument will result in the
 * caller being "greeted" with a {@link NullPointerException}.</p>
 */
// TODO: that should be an abstract class, really
@ParametersAreNonnullByDefault
public interface FileSystemRepository
{
    /**
     * Create a filesystem driver for a particular URI and configuration
     *
     * <p>When this method is called, the URI is guaranteed to be well formed
     * (ie, a hierarchical URI).</p>
     *
     * @param uri the URI
     * @param env the environment
     * @return a filesystem driver
     * @throws IOException driver could not be created
     *
     * @see FileSystemProvider#newFileSystem(URI, Map)
     */
    @Nonnull
    FileSystemDriver createDriver(URI uri, Map<String, ?> env)
        throws IOException;

    /**
     * Return the scheme associated with this provider
     *
     * @return the scheme
     *
     * @see FileSystemProvider#getScheme()
     */
    @Nonnull
    String getScheme();

    /**
     * Create a new filesystem
     *
     * @param provider the associated provider
     * @param uri the URI
     * @param env the filesystem configuration
     * @return a new filesystem
     * @throws IOException failure to create the filesystem
     *
     * @see FileSystemProvider#newFileSystem(URI, Map)
     */
    // TODO: not sure about this and createDriver
    @Nonnull
    FileSystem createFileSystem(FileSystemProvider provider, URI uri,
        Map<String, ?> env)
        throws IOException;

    /**
     * Get a filesystem associated with a URI
     *
     * @param uri the URI
     * @return the filesystem
     * @throws FileSystemNotFoundException filesystem does not exist and cannot
     * be automatically created
     *
     * @see FileSystemProvider#getFileSystem(URI)
     */
    @Nonnull
    FileSystem getFileSystem(URI uri);

    /**
     * Get a path associated to a URI
     *
     * @param uri the URI
     * @return the path
     */
    @Nonnull
    Path getPath(URI uri);

    // Called ONLY after the driver and fs have been successfully closed
    // uri is guaranteed to exist
    void unregister(URI uri);
}
