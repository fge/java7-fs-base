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

import com.github.fge.filesystem.driver.metadata.AttributesProvider;
import com.github.fge.filesystem.driver.metadata.AttributeFactory;
import com.github.fge.filesystem.driver.metadata.MetadataDriver;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class FileAttributeViewBaseWithAttributes<
    D extends MetadataDriver<M>, M, A extends BasicFileAttributes>
    extends FileAttributeViewBase<D, M>
    implements AttributesProvider<A>
{
    private final Object lock = new Object();
    private A attributes = null;

    protected FileAttributeViewBaseWithAttributes(final String name,
        final Path path, final AttributeFactory<D, M> factory)
    {
        super(name, path, factory);
    }

    @Override
    public final A readAttributes()
        throws IOException
    {
        synchronized (lock) {
            if (attributes == null)
                attributes = factory.readAttributes(path, name);
            return attributes;
        }
    }
}
