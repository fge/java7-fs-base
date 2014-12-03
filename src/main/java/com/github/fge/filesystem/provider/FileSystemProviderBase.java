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

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.spi.FileSystemProvider;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class FileSystemProviderBase
    extends FileSystemProvider
{
    private final String scheme;

    protected FileSystemProviderBase(final String scheme)
    {
        // TODO: validity of the scheme is not checked
        this.scheme = Objects.requireNonNull(scheme);
    }

    /**
     * Returns the URI scheme that identifies this provider.
     *
     * @return The URI scheme
     */
    @Override
    public final String getScheme()
    {
        return scheme;
    }

}
