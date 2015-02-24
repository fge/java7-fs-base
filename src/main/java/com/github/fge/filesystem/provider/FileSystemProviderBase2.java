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

package com.github.fge.filesystem.provider;

import com.github.fge.filesystem.driver.metadata.AttributeFactory;
import com.github.fge.filesystem.driver.metadata.write.ViewWriter;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Objects;

public abstract class FileSystemProviderBase2
    extends FileSystemProvider
{
    protected final AttributeFactory<?, ?> attributeFactory;

    protected FileSystemProviderBase2(
        final AttributeFactory<?, ?> attributeFactory)
    {
        this.attributeFactory = Objects.requireNonNull(attributeFactory);
    }

    @Override
    public void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        final String viewName, attrName;
        final int colonIndex = attribute.indexOf(':');

        if (colonIndex == -1) {
            viewName = "basic";
            attrName = attribute;
        } else {
            viewName = attribute.substring(0, colonIndex);
            attrName = attribute.substring(colonIndex + 1);
        }

        final ViewWriter<?> writer = attributeFactory.getWriter(path, viewName);
        writer.setAttributeByName(attrName, value);
    }

    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        // TODO
        return null;
    }
}
