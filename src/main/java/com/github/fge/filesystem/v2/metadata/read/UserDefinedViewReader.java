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

package com.github.fge.filesystem.v2.metadata.read;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
public final class UserDefinedViewReader
    extends ViewReader<UserDefinedFileAttributeView>
{
    public UserDefinedViewReader(final UserDefinedFileAttributeView view)
    {
        super(view);
    }

    @Nullable
    @Override
    public Object getAttributeByName(final String name)
        throws IOException
    {
        final int size = view.size(name);
        final ByteBuffer buf = ByteBuffer.allocate(size);
        view.read(name, buf);
        buf.flip();
        return buf;
    }

    @Nonnull
    @Override
    public Map<String, Object> getAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        for (final String name: view.list())
            map.put(name, getAttributeByName(name));

        return Collections.unmodifiableMap(map);
    }
}
