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
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * File attribute descriptors for all {@link FileAttributeView}s defined by
 * the JDK
 */
public enum StandardAttributesDescriptor
    implements AttributesDescriptor
{
    ACL("acl", AclFileAttributeView.class, null),
    BASIC("basic", BasicFileAttributeView.class, BasicFileAttributes.class),
    DOS("dos", DosFileAttributeView.class, DosFileAttributes.class),
    FILE_OWNER("owner", FileOwnerAttributeView.class, null),
    POSIX("posix", PosixFileAttributeView.class, PosixFileAttributes.class),
    USER("user", UserDefinedFileAttributeView.class, null)
    ;

    private final String name;
    private final Class<? extends FileAttributeView> viewClass;
    private final Class<? extends BasicFileAttributes> attributeClass;

    StandardAttributesDescriptor(final String name,
        final Class<? extends FileAttributeView> viewClass,
        final Class<? extends BasicFileAttributes> attributeClass)
    {
        this.name = name;
        this.viewClass = viewClass;
        this.attributeClass = attributeClass;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return name;
    }

    @Nonnull
    @Override
    public Class<? extends FileAttributeView> getViewClass()
    {
        return viewClass;
    }

    @Nullable
    @Override
    public Class<? extends BasicFileAttributes> getAttributeClass()
    {
        return attributeClass;
    }
}
