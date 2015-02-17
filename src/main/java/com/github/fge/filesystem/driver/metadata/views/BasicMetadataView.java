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

package com.github.fge.filesystem.driver.metadata.views;

import com.github.fge.filesystem.driver.metadata.AttributeWriterByName;
import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import com.github.fge.filesystem.driver.metadata.writers.BasicAttributeWriter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

@ParametersAreNonnullByDefault
public class BasicMetadataView<M>
    extends MetadataView<M>
    implements BasicFileAttributeView, AttributeWriterByName
{
    private final String name = "basic";
    protected final BasicAttributeWriter<M> writer;

    public BasicMetadataView(final Path path, final MetadataDriver<M> driver)
    {
        super(path, driver);
        writer = driver.getAttributeWriter(path, name);
    }

    @Override
    public final String name()
    {
        return name;
    }

    @Override
    public final BasicFileAttributes readAttributes()
        throws IOException
    {
        return driver.getAttributesByName(path, name);
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        writer.setAttributeByName(name, value);
    }

    @Override
    public final void setTimes(@Nullable final FileTime lastModifiedTime,
        @Nullable final FileTime lastAccessTime,
        @Nullable final FileTime createTime)
        throws IOException
    {
        writer.setTimes(lastModifiedTime, lastAccessTime, createTime);
    }
}
