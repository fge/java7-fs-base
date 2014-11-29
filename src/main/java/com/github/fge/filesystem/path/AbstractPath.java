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

package com.github.fge.filesystem.path;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;

@ParametersAreNonnullByDefault
public abstract class AbstractPath
    implements Path
{
    protected final FileSystem fs;

    protected final PathNamesFactory factory;
    protected final PathNames pathNames;

    protected AbstractPath(final FileSystem fs, final PathNamesFactory factory,
        final PathNames pathNames)
    {
        this.fs = fs;
        this.factory = factory;
        this.pathNames = pathNames;
    }

    @Override
    public final FileSystem getFileSystem()
    {
        return fs;
    }

    private void checkProvider(final Path other)
    {
        if (!fs.provider().equals(other.getFileSystem().provider()))
            throw new ProviderMismatchException();
    }
}
