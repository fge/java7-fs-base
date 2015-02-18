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
import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class UserDefinedMetadataView<M>
    extends BaseMetadataView<M>
    implements UserDefinedFileAttributeView, AttributeReaderByName,
    AttributeWriterByName
{
    protected UserDefinedMetadataView(final Path path,
        final MetadataDriver<M> driver)
    {
        super("user", path, driver);
    }

    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        if (!list().contains(Objects.requireNonNull(name)))
            throw new NoSuchAttributeException(name);

        final ByteBuffer buf = ByteBuffer.allocate(size(name));
        read(name, buf);
        return buf.array();
    }

    @Nonnull
    @Override
    public final Map<String, Object> getAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        ByteBuffer buf;

        for (final String attrName: list()) {
            buf = ByteBuffer.allocate(size(attrName));
            write(attrName, buf);
            map.put(attrName, buf.array());
        }

        return Collections.unmodifiableMap(map);
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        /*
         * See javadoc for UserDefinedFileAttributeView; argument can either
         * be a ByteBuffer or a byte array.
         *
         * Since ClassCastException can be thrown by .setAttribute(), we can
         * take the risk of the instanceof test and subsequent cast.
         */
        final ByteBuffer buf = value instanceof  ByteBuffer
            ? (ByteBuffer) value
            : ByteBuffer.wrap((byte[]) value);

        write(name, buf);
    }
}
