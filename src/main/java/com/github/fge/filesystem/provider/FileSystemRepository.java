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

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

public interface FileSystemRepository
{
    @Nonnull
    String getScheme();

    @Nonnull
    FileSystem createFileSystem(FileSystemProvider provider, URI uri,
        Map<String, ?> env)
        throws IOException;

    @Nonnull
    FileSystem getFileSystem(URI uri);

    @Nonnull
    FileSystemFactoryProvider getFactoryProvider();

    // Note: fs never created automatically
    @Nonnull
    Path getPath(URI uri);

    // Called ONLY after the driver and fs have been successfully closed
    // uri is guaranteed to exist
    void unregister(URI uri);
}
