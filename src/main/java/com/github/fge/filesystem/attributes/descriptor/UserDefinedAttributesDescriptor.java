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

package com.github.fge.filesystem.attributes.descriptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Objects;
import java.util.Set;

public enum UserDefinedAttributesDescriptor
    implements AttributesDescriptor
{
    INSTANCE;

    @Nonnull
    @Override
    public String getName()
    {
        return "user";
    }

    @Nonnull
    @Override
    public Class<? extends FileAttributeView> getViewClass()
    {
        return UserDefinedFileAttributeView.class;
    }

    @Nullable
    @Override
    public Class<? extends BasicFileAttributes> getAttributeClass()
    {
        return null;
    }

    @Nullable
    @Override
    public Set<String> getAttributeNames()
    {
        return null;
    }

    @Nonnull
    @Override
    public Access getAccess(@Nonnull final String attributeName)
    {
        Objects.requireNonNull(attributeName);
        return Access.MAYBE;
    }
}
