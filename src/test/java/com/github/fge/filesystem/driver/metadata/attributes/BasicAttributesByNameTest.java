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

import java.nio.file.attribute.BasicFileAttributes;

public final class BasicAttributesByNameTest
    extends AttributesByNameTest
{
    public BasicAttributesByNameTest()
    {
        super(
            BasicFileAttributes.class,
            "lastModifiedTime",
            "lastAccessTime",
            "creationTime",
            "size",
            "isRegularFile",
            "isDirectory",
            "isSymbolicLink",
            "isOther",
            "fileKey"
        );
    }

    @BeforeMethod
    @Override
    protected void initAttributes()
    {
        attrs = new BasicAttributes<Object>(pathMetadata)
        {
            @Override
            public long size()
            {
                return 0L;
            }
        };
    }
}
