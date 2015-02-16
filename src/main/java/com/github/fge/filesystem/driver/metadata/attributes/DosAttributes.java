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

import com.github.fge.filesystem.driver.metadata.PathMetadata;

import javax.annotation.Nonnull;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("DesignForExtension")
public abstract class DosAttributes<M>
    extends BasicAttributes<M>
    implements DosFileAttributes
{
    protected DosAttributes(final PathMetadata<M> pathMetadata)
    {
        super(pathMetadata);
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

    @Override
    public Object getAttributeByName(@Nonnull final String name)
    {
        switch (Objects.requireNonNull(name)) {
            case "readonly":
                return isReadOnly();
            case "hidden":
                return isHidden();
            case "system":
                return isSystem();
            case "archive":
                return isArchive();
            default:
                return super.getAttributeByName(name);
        }
    }

    @Nonnull
    @Override
    public Map<String, Object> getAllAttributes()
    {
        final Map<String, Object> map = new HashMap<>(super.getAllAttributes());

        map.put("readonly", isReadOnly());
        map.put("hidden", isHidden());
        map.put("system", isSystem());
        map.put("archive", isArchive());

        return Collections.unmodifiableMap(map);
    }
}
