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

import com.github.fge.filesystem.attributes.wrap.read.FileAttributesReader;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class DelegatingFileAttributeWrapper<V extends FileAttributeView, A>
    extends FileAttributeWrapper<V>
{
    protected final FileAttributesReader<A> reader;

    protected DelegatingFileAttributeWrapper(final V view,
        final FileAttributesReader<A> reader)
    {
        super(view);
        this.reader = reader;
    }

    @Nullable
    @Override
    public final Object readAttribute(final String name)
        throws IOException
    {
        return reader.readAttribute(Objects.requireNonNull(name));
    }
}
