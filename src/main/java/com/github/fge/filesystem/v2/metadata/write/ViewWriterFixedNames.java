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

package com.github.fge.filesystem.v2.metadata.write;

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public abstract class ViewWriterFixedNames<V extends FileAttributeView>
    extends ViewWriter<V>
{
    protected final Set<String> attrNames = new HashSet<>();

    protected ViewWriterFixedNames(final V view, final String... names)
    {
        super(view);
        for (final String name: names)
            attrNames.add(Objects.requireNonNull(name));
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        if (!attrNames.contains(name))
            throw new NoSuchAttributeException(name);

        doSetAttributeByName(name, value);
    }

    protected abstract void doSetAttributeByName(final String name,
        final Object value)
        throws IOException;
}
