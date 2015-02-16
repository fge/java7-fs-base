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

package com.github.fge.filesystem.driver.metadata.readers;

import com.github.fge.filesystem.driver.metadata.MetadataDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class AclAttributeReader<M>
    extends FileOwnerAttributeReader<M>
{
    protected AclAttributeReader(final Path path,
        final MetadataDriver<M> driver)
    {
        super(path, driver);
    }

    @Nullable
    @Override
    public Object getAttributeByName(final String name)
        throws IOException
    {
        return "acl".equals(Objects.requireNonNull(name))
            ? getAcl()
            : super.getAttributeByName(name);
    }

    @Nonnull
    @Override
    public Map<String, Object> getAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>(super.getAllAttributes());
        map.put("acl", getAcl());
        return Collections.unmodifiableMap(map);
    }

    public abstract List<AclEntry> getAcl()
        throws IOException;
}
