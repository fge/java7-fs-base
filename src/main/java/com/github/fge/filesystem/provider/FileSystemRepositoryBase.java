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
import com.github.fge.filesystem.fs.FileSystemBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

// TODO: need to find a way to remove a FileSystem/driver on close
@ParametersAreNonnullByDefault
public abstract class FileSystemRepositoryBase
    implements FileSystemRepository
{
    private final String scheme;
    private final Map<URI, FileSystemBase> filesystems = new HashMap<>();

    protected FileSystemRepositoryBase(final String scheme)
    {
        this.scheme = scheme;
    }

    @Override
    public final String getScheme()
    {
        return scheme;
    }

    @Override
    public final FileSystem createFileSystem(final FileSystemProvider provider,
        final URI uri, final Map<String, ?> env)
        throws IOException
    {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(env);
        checkURI(uri);

        synchronized (filesystems) {
            if (filesystems.containsKey(uri))
                throw new FileSystemAlreadyExistsException();
            final FileSystemDriver driver = createDriver(uri, env);
            final FileSystemBase fs = new FileSystemBase(this, driver,
                provider);
            filesystems.put(uri, fs);
            return fs;
        }
    }

    @Override
    public final FileSystem getFileSystem(final URI uri)
    {
        checkURI(uri);

        final FileSystem fs;

        synchronized (filesystems) {
            fs = filesystems.get(uri);
        }

        if (fs == null)
            throw new FileSystemNotFoundException();

        return fs;
    }

    // Called ONLY after the driver and fs have been successfully closed
    // uri is guaranteed to exist
    @Override
    public final void unregister(final URI uri)
    {
        Objects.requireNonNull(uri);
        synchronized (filesystems) {
            filesystems.remove(uri);
        }
    }

    private static void checkURI(@Nullable final URI uri)
    {
        Objects.requireNonNull(uri);
        if (!uri.isAbsolute())
            throw new IllegalArgumentException("uri is not absolute");
        if (uri.isOpaque())
            throw new IllegalArgumentException("uri is not hierarchical "
                + "(.isOpaque() returns true)");
    }
}
