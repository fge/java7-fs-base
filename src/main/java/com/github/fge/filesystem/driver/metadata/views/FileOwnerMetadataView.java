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

import com.github.fge.filesystem.driver.metadata.AttributeReaderByName;
import com.github.fge.filesystem.driver.metadata.AttributeWriterByName;
import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import com.github.fge.filesystem.driver.metadata.readers.FileOwnerAttributeReader;
import com.github.fge.filesystem.driver.metadata.writers.FileOwnerAttributeWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Map;

@ParametersAreNonnullByDefault
public class FileOwnerMetadataView<M>
    extends MetadataView<M>
    implements FileOwnerAttributeView, AttributeReaderByName,
    AttributeWriterByName
{
    private final String name = "owner";
    protected final FileOwnerAttributeReader<M> reader;
    protected final FileOwnerAttributeWriter<M> writer;

    public FileOwnerMetadataView(final Path path,
        final MetadataDriver<M> driver)
    {
        super(path, driver);
        reader = driver.getAttributeReader(path, name);
        writer = driver.getAttributeWriter(path, name);
    }

    @Override
    public final String name()
    {
        return name;
    }

    @Override
    public final UserPrincipal getOwner()
        throws IOException
    {
        return reader.getOwner();
    }

    @Nonnull
    @Override
    public final Map<String, Object> getAllAttributes()
        throws IOException
    {
        return reader.getAllAttributes();
    }

    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        return reader.getAttributeByName(name);
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        writer.setAttributeByName(name, value);
    }

    @Override
    public final void setOwner(final UserPrincipal owner)
        throws IOException
    {
        writer.setOwner(owner);
    }
}
