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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public enum StandardAttributesDescriptor
    implements AttributesDescriptor
{
    ACL(
        "acl",
        AclFileAttributeView.class,
        null,
        Collections.<String>emptyList(),
        Arrays.asList("acl", "owner")
    ),
    BASIC(
        "basic",
        BasicFileAttributeView.class,
        BasicFileAttributes.class,
        Arrays.asList("size", "isRegularFile", "isDirectory", "isSymbolicLink",
            "isOther", "fileKey"),
        Arrays.asList("lastModifiedTime", "lastAccessTime", "creationTime")
    ),
    DOS(
        "dos",
        DosFileAttributeView.class,
        DosFileAttributes.class,
        Arrays.asList(
            "size", "isRegularFile", "isDirectory", "isSymbolicLink",
            "isOther", "fileKey"
        ),
        Arrays.asList(
            "lastModifiedTime", "lastAccessTime", "creationTime",
            "hidden", "system", "archive", "readonly"
        )
    ),
    FILE_OWNER(
        "owner",
        FileOwnerAttributeView.class,
        null,
        Collections.<String>emptyList(),
        Collections.singletonList("owner")
    ),
    POSIX(
        "posix",
        PosixFileAttributeView.class,
        PosixFileAttributes.class,
        Arrays.asList(
            "size", "isRegularFile", "isDirectory", "isSymbolicLink",
            "isOther", "fileKey"
        ),
        Arrays.asList(
            "lastModifiedTime", "lastAccessTime", "creationTime",
            "owner", "group", "permissions"
        )
    ),
    ;

    private final String name;
    private final Class<? extends FileAttributeView> viewClass;
    private final Class<?> readAttributesClass;
    private final Map<String, Access> attributes = new HashMap<>();

    StandardAttributesDescriptor(final String name,
        final Class<? extends FileAttributeView> viewClass,
        @Nullable final Class<?> readAttributesClass,
        final List<String> readOnlyAttributes,
        final List<String> readWriteAttributes)
    {
        this.name = name;
        this.viewClass = viewClass;
        this.readAttributesClass = readAttributesClass;
        for (final String attr: readOnlyAttributes)
            attributes.put(Objects.requireNonNull(attr), Access.READ_ONLY);
        for (final String attr: readWriteAttributes)
            attributes.put(Objects.requireNonNull(attr), Access.READ_WRITE);
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
    public Class<?> getReadAttributesClass()
    {
        return readAttributesClass;
    }

    @Nullable
    @Override
    public Set<String> getAllAttributes()
    {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    @Nonnull
    @Override
    public Access getAccess(@Nonnull final String attributeName)
    {
        final Access access = attributes.get(attributeName);
        return access != null ? access : Access.UNKNOWN;
    }
}
