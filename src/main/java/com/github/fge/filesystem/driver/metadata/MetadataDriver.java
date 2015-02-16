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

package com.github.fge.filesystem.driver.metadata;

import com.github.fge.filesystem.driver.metadata.readers.AttributeReader;
import com.github.fge.filesystem.driver.metadata.writers.AttributeWriter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

/**
 * Base class for a metadata driver
 *
 * @param <M> type of the metadata class
 */
@ParametersAreNonnullByDefault
public abstract class MetadataDriver<M>
{
    public abstract PathMetadata<M> getMetaData(Path path)
        throws IOException;

    public abstract void writeMetadata(PathMetadata<M> metadata)
        throws IOException;

    public abstract <V extends FileAttributeView> V getViewByClass(Path path,
        Class<V> viewClass);

    public abstract <V extends FileAttributeView> V getViewByName(Path path,
        String name);

    public abstract <A extends BasicFileAttributes> A getAttributesByClass(
        Path path, Class<A> attributesClass)
        throws IOException;

    public abstract <A extends BasicFileAttributes> A getAttributesByName(
        Path path, String name)
        throws IOException;

    public abstract <W extends AttributeWriter<M>> W getAttributeWriter(
        Path path, String name);

    public abstract <R extends AttributeReader<M>> R getAttributeReader(
        Path path, String name);
}
