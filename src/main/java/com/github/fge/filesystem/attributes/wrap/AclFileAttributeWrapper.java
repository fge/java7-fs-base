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

package com.github.fge.filesystem.attributes.wrap;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class AclFileAttributeWrapper
    extends FileAttributeWrapper<AclFileAttributeView>
{
    public AclFileAttributeWrapper(final AclFileAttributeView view)
    {
        super(view);
    }

    @Nullable
    @Override
    public Object readAttribute(final String name)
        throws IOException
    {
        switch (Objects.requireNonNull(name)) {
            /* owner */
            case "owner":
                return view.getOwner();
            case "acl":
                return view.getAcl();
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }

    @Override
    public void writeAttribute(final String name, @Nullable final Object value)
        throws IOException
    {
        switch (Objects.requireNonNull(name)) {
            /* owner */
            case "owner":
                view.setOwner((UserPrincipal) value);
                break;
            /* owner */
            case "acl":
                //noinspection unchecked
                view.setAcl((List<AclEntry>) value);
                break;
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }
}
