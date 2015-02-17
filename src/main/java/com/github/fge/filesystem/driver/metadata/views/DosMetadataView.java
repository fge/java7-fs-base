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
import com.github.fge.filesystem.driver.metadata.writers.DosAttributeWriter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;

@ParametersAreNonnullByDefault
public class DosMetadataView<M>
    extends MetadataView<M>
    implements DosFileAttributeView, AttributeWriterByName
{
    protected final DosAttributeWriter<M> writer;

    public DosMetadataView(final Path path, final MetadataDriver<M> driver)
    {
        super("dos", path, driver);
        writer = driver.getAttributeWriter(path, name);
    }

    @Override
    public final DosFileAttributes readAttributes()
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

    @Override
    public final void setReadOnly(final boolean value)
        throws IOException
    {
        writer.setReadOnly(value);
    }

    @Override
    public final void setHidden(final boolean value)
        throws IOException
    {
        writer.setHidden(value);
    }

    @Override
    public final void setSystem(final boolean value)
        throws IOException
    {
        writer.setSystem(value);
    }

    @Override
    public final void setArchive(final boolean value)
        throws IOException
    {
        writer.setArchive(value);
    }
}
