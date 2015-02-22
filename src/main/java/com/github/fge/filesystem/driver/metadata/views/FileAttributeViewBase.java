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

import com.github.fge.filesystem.driver.metadata.AttributeFactory;
import com.github.fge.filesystem.driver.metadata.MetadataDriver;

import java.nio.file.Path;
import java.nio.file.attribute.FileAttributeView;
import java.util.Objects;

public abstract class FileAttributeViewBase<D extends MetadataDriver<M>, M>
    implements FileAttributeView
{
    protected final String name;

    protected final AttributeFactory<D, M> factory;
    protected final D driver;
    protected final Path path;

    protected FileAttributeViewBase(final String name, final Path path,
        final AttributeFactory<D, M> factory)
    {
        this.name = Objects.requireNonNull(name);
        this.path = path;
        this.factory = Objects.requireNonNull(factory);
        driver = factory.getDriver();
    }

    @Override
    public final String name()
    {
        return name;
    }
}
