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

package com.github.fge.filesystem.driver.oldmetadata.writers;

import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;
import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({ "DesignForExtension", "MethodMayBeStatic" })
@ParametersAreNonnullByDefault
public abstract class PosixAttributeWriter<M>
    extends BasicAttributeWriter<M>
{
    protected PosixAttributeWriter(final Path path,
        final MetadataDriver<M> driver)
    {
        super(path, driver);
    }

    @Override
    public void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        Objects.requireNonNull(value);
        switch (Objects.requireNonNull(name)) {
            case "owner":
                setOwner((UserPrincipal) value);
                break;
            case "group":
                setGroup((GroupPrincipal) value);
                break;
            case "permissions":
                //noinspection unchecked
                setPermissions((Set<PosixFilePermission>) value);
                break;
            default:
                super.setAttributeByName(name, value);
        }
    }

    public void setPermissions(final Set<PosixFilePermission> perms)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    public void setGroup(final GroupPrincipal group)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    public void setOwner(final UserPrincipal owner)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }
}
