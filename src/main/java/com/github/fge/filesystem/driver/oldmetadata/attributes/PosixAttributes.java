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

package com.github.fge.filesystem.driver.oldmetadata.attributes;

import com.github.fge.filesystem.driver.oldmetadata.PathMetadata;

import javax.annotation.Nonnull;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("DesignForExtension")
public abstract class PosixAttributes<M>
    extends BasicAttributes<M>
    implements PosixFileAttributes
{
    protected PosixAttributes(final PathMetadata<M> pathMetadata)
    {
        super(pathMetadata);
    }

    @Override
    public Object getAttributeByName(final String name)
    {
        switch (Objects.requireNonNull(name)) {
            case "owner":
                return owner();
            case "group":
                return group();
            case "permissions":
                return permissions();
            default:
                return super.getAttributeByName(name);
        }
    }

    @Override
    @Nonnull
    public Map<String, Object> getAllAttributes()
    {
        final Map<String, Object> map = new HashMap<>(super.getAllAttributes());

        map.put("owner", owner());
        map.put("group", group());
        map.put("permissions", permissions());

        return Collections.unmodifiableMap(map);
    }
}
