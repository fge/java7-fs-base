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

package com.github.fge.filesystem.v2.metadata.attributes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.DosFileAttributes;

@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class DosFileAttributesBase<M>
    extends BasicFileAttributesBase<M>
    implements DosFileAttributes
{
    protected DosFileAttributesBase(final M metadata)
    {
        super(metadata);
    }

    @Override
    public boolean isReadOnly()
    {
        return false;
    }

    @Override
    public boolean isArchive()
    {
        return false;
    }

    @Override
    public boolean isSystem()
    {
        return false;
    }

    @Override
    public boolean isHidden()
    {
        return false;
    }
}
