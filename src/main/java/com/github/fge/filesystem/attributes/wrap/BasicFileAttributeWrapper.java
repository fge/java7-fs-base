/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.attributes.wrap;

import com.github.fge.filesystem.attributes.wrap.read.BasicFileAttributesReader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

// TODO: duplicate attribute names with FileAttributeReader
@ParametersAreNonnullByDefault
public final class BasicFileAttributeWrapper
    extends DelegatingFileAttributeWrapper<BasicFileAttributeView, BasicFileAttributes>
{
    public BasicFileAttributeWrapper(final BasicFileAttributeView view)
        throws IOException
    {
        super(view, new BasicFileAttributesReader(view.readAttributes()));
    }

    @Override
    public void writeAttribute(final String name, @Nullable final Object value)
        throws IOException
    {
        switch (Objects.requireNonNull(name)) {
            case "lastModifiedTime":
                view.setTimes((FileTime) value, null, null);
                break;
            case "lastAccessTime":
                view.setTimes(null, (FileTime) value, null);
                break;
            case "creationTime":
                view.setTimes(null, null, (FileTime) value);
                break;
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }
}
