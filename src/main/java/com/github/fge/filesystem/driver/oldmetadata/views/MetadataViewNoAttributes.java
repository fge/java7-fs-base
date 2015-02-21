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

package com.github.fge.filesystem.driver.oldmetadata.views;

import com.github.fge.filesystem.driver.oldmetadata.AttributeReaderByName;
import com.github.fge.filesystem.driver.oldmetadata.AttributeWriterByName;
import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;
import com.github.fge.filesystem.driver.oldmetadata.readers.AttributeReader;
import com.github.fge.filesystem.driver.oldmetadata.writers.AttributeWriter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

@ParametersAreNonnullByDefault
public abstract class MetadataViewNoAttributes<M, R extends AttributeReader<M>, W extends AttributeWriter<M>>
    extends BaseMetadataView<M>
    implements AttributeReaderByName, AttributeWriterByName
{
    protected final R reader;
    protected final W writer;

    protected MetadataViewNoAttributes(final String name, final Path path,
        final MetadataDriver<M> driver)
    {
        super(name, path, driver);
        reader = driver.getAttributeReader(path, name);
        writer = driver.getAttributeWriter(path, name);
    }

    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        return reader.getAttributeByName(name);
    }

    @Nonnull
    @Override
    public final Map<String, Object> getAllAttributes()
        throws IOException
    {
        return reader.getAllAttributes();
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        writer.setAttributeByName(name, value);
    }
}
