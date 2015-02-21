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

package com.github.fge.filesystem.driver.oldmetadata.readers;

import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;
import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class FileOwnerAttributeReader<M>
    extends AttributeReader<M>
{
    protected FileOwnerAttributeReader(final Path path,
        final MetadataDriver<M> driver)
    {
        super(path, driver);
    }

    @Nullable
    @Override
    public Object getAttributeByName(final String name)
        throws IOException
    {
        if (!"owner".equals(Objects.requireNonNull(name)))
            throw new NoSuchAttributeException(name);
        return getOwner();
    }

    @Nonnull
    @Override
    public Map<String, Object> getAllAttributes()
        throws IOException
    {
        return Collections.<String, Object>singletonMap("owner", getOwner());
    }

    public abstract UserPrincipal getOwner()
        throws IOException;
}
