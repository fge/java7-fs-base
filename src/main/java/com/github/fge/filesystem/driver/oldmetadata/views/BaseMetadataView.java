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

import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;

import java.nio.file.Path;
import java.nio.file.attribute.FileAttributeView;

/**
 * Marker interface for metadata views
 *
 * <p>The generic parameter is unused...</p>
 *
 * @param <M> metadata class
 */
public abstract class BaseMetadataView<M>
    implements FileAttributeView
{
    protected final String name;
    protected final Path path;
    protected final MetadataDriver<M> driver;

    protected BaseMetadataView(final String name, final Path path,
        final MetadataDriver<M> driver)
    {
        this.name = name;
        this.path = path;
        this.driver = driver;
    }

    @Override
    public final String name()
    {
        return name;
    }
}
