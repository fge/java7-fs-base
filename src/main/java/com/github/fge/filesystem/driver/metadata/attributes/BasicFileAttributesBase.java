/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.driver.metadata.attributes;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 *
 * @param <M> metadata class
 */
@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class BasicFileAttributesBase<M>
    implements BasicFileAttributes
{
    protected static final FileTime UNIX_EPOCH = FileTime.fromMillis(0L);

    protected final M metadata;

    protected BasicFileAttributesBase(final M metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public FileTime lastModifiedTime()
    {
        return UNIX_EPOCH;
    }

    @Override
    public FileTime lastAccessTime()
    {
        return UNIX_EPOCH;
    }

    @Override
    public FileTime creationTime()
    {
        return UNIX_EPOCH;
    }

    @Nullable
    @Override
    public Object fileKey()
    {
        return null;
    }
}
