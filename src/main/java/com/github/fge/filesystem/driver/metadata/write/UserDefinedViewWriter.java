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

package com.github.fge.filesystem.driver.metadata.write;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;

@ParametersAreNonnullByDefault
public final class UserDefinedViewWriter
    extends ViewWriter<UserDefinedFileAttributeView>
{
    public UserDefinedViewWriter(final UserDefinedFileAttributeView view)
    {
        super(view);
    }

    @Override
    public void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        final ByteBuffer buf = value instanceof byte[]
            ? ByteBuffer.wrap((byte[]) value)
            : (ByteBuffer) value;

        view.write(name, buf);
    }
}
