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
import com.github.fge.filesystem.driver.metadata.writers.AttributeWriter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@ParametersAreNonnullByDefault
public abstract class MetadataViewWithAttributes<M, W extends AttributeWriter<M>, A extends BasicFileAttributes>
    extends BaseMetadataView<M>
    implements AttributeWriterByName
{
    protected final W writer;
    protected final Class<A> attributesClass;

    protected MetadataViewWithAttributes(final String name, final Path path,
        final MetadataDriver<M> driver, final Class<A> attributesClass)
    {
        super(name, path, driver);
        this.attributesClass = attributesClass;
        writer = driver.getAttributeWriter(path, name);
    }

    public final A readAttributes()
        throws IOException
    {
        return driver.getAttributesByClass(path, attributesClass);
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        writer.setAttributeByName(name, value);
    }
}
