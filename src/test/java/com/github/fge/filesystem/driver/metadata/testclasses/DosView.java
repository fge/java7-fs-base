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

package com.github.fge.filesystem.driver.metadata.testclasses;

import com.github.fge.filesystem.driver.metadata.AttributeFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;

public final class DosView
    implements DosFileAttributeView
{
    public DosView(final Path path, final AttributeFactory<?, ?> factory)
    {
    }

    @Override
    public String name()
    {
        return null;
    }

    @Override
    public DosFileAttributes readAttributes()
        throws IOException
    {
        return null;
    }

    @Override
    public void setReadOnly(final boolean value)
        throws IOException
    {
    }

    @Override
    public void setHidden(final boolean value)
        throws IOException
    {
    }

    @Override
    public void setSystem(final boolean value)
        throws IOException
    {
    }

    @Override
    public void setArchive(final boolean value)
        throws IOException
    {
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
    }
}