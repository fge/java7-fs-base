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

import org.testng.annotations.BeforeMethod;

import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

public final class PosixAttributesByNameTest
    extends AttributesByNameTest
{
    public PosixAttributesByNameTest()
    {
        super(
            PosixFileAttributes.class,
            "lastModifiedTime",
            "lastAccessTime",
            "creationTime",
            "size",
            "isRegularFile",
            "isDirectory",
            "isSymbolicLink",
            "isOther",
            "fileKey",
            "owner",
            "group",
            "permissions"
        );
    }


    @BeforeMethod
    @Override
    protected void initAttributes()
    {
        attrs = new PosixAttributes<Object>(pathMetadata)
        {
            @Override
            public UserPrincipal owner()
            {
                return null;
            }

            @Override
            public GroupPrincipal group()
            {
                return null;
            }

            @Override
            public Set<PosixFilePermission> permissions()
            {
                return null;
            }

            @Override
            public long size()
            {
                return 0L;
            }
        };
    }
}
