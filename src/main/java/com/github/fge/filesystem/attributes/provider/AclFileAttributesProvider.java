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

package com.github.fge.filesystem.attributes.provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class AclFileAttributesProvider<V extends AclFileAttributeView>
    implements FileAttributesProvider<V, Void>
{
    private final V view;

    protected AclFileAttributesProvider(final V view)
    {
        this.view = Objects.requireNonNull(view);
    }

    @Nonnull
    @Override
    public final V getAttributeView()
    {
        return view;
    }

    @Nullable
    @Override
    public final Void getAttributes()
        throws IOException
    {
        return null;
    }

    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        switch (name) {
            /* owner */
            case "owner":
                return view.getOwner();
            /* acl */
            case "acl":
                return view.getAcl();
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }

    @Override
    public final void setAttributeByName(final String name,
        @Nullable final Object value)
        throws IOException
    {
        switch (name) {
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
