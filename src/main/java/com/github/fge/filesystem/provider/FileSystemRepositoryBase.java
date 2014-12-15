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

import com.github.fge.filesystem.configuration.FileSystemFactoryProvider;
import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.fs.GenericFileSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class FileSystemRepositoryBase
    implements FileSystemRepository
{
    private final String scheme;
    private final FileSystemFactoryProvider factoryProvider;
    private final Map<URI, GenericFileSystem> filesystems = new HashMap<>();

    protected FileSystemRepositoryBase(final String scheme,
        final FileSystemFactoryProvider factoryProvider)
    {
        this.scheme = Objects.requireNonNull(scheme);
        this.factoryProvider = Objects.requireNonNull(factoryProvider);
    }

    @Override
    @Nonnull
    public final String getScheme()
    {
        return scheme;
    }

    @Nonnull
    @Override
    public final FileSystemFactoryProvider getFactoryProvider()
    {
        return factoryProvider;
    }

    @Nonnull
    protected abstract FileSystemDriver createDriver(URI uri,
        Map<String, ?> env)
        throws IOException;

    @Override
    @Nonnull
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
            final GenericFileSystem fs
                = new GenericFileSystem(uri, this, driver, provider);
            filesystems.put(uri, fs);
            return fs;
        }
    }

    @Override
    @Nonnull
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

    // Note: fs never created automatically
    @Override
    @Nonnull
    public final Path getPath(final URI uri)
    {
        checkURI(uri);

        URI tmp;
        GenericFileSystem fs;
        String path;

        synchronized (filesystems) {
            for (final Map.Entry<URI, GenericFileSystem> entry:
                filesystems.entrySet()) {
                tmp = uri.relativize(entry.getKey());
                if (tmp.isAbsolute())
                    continue;
                fs = entry.getValue();
                // TODO: can happen...
                if (!fs.isOpen())
                    continue;
                path = tmp.getPath();
                if (path == null)
                    path = "";
                return entry.getValue().getPath(path);
            }
        }

        throw new FileSystemNotFoundException();
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

    // TODO: should be checked at the provider level, not here
    private void checkURI(@Nullable final URI uri)
    {
        Objects.requireNonNull(uri);
        if (!uri.isAbsolute())
            throw new IllegalArgumentException("uri is not absolute");
        if (uri.isOpaque())
            throw new IllegalArgumentException("uri is not hierarchical "
                + "(.isOpaque() returns true)");
        if (!scheme.equals(uri.getScheme()))
            throw new IllegalArgumentException("bad scheme");
    }
}
