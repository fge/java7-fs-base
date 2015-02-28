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

package com.github.fge.filesystem.v2.metadata.testclasses;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public final class AttrsConstructorBadAccess
    implements BasicFileAttributes
{
    AttrsConstructorBadAccess(final Object object)
    {
    }

    @Override
    public FileTime lastModifiedTime()
    {
        return null;
    }

    @Override
    public FileTime lastAccessTime()
    {
        return null;
    }

    @Override
    public FileTime creationTime()
    {
        return null;
    }

    @Override
    public boolean isRegularFile()
    {
        return false;
    }

    @Override
    public boolean isDirectory()
    {
        return false;
    }

    @Override
    public boolean isSymbolicLink()
    {
        return false;
    }

    @Override
    public boolean isOther()
    {
        return false;
    }

    @Override
    public long size()
    {
        return 0L;
    }

    @Override
    public Object fileKey()
    {
        return null;
    }
}
