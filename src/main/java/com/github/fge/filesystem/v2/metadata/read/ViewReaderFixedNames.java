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

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public abstract class ViewReaderFixedNames<V extends FileAttributeView>
    extends ViewReader<V>
{
    protected final Set<String> attrNames = new HashSet<>();

    protected ViewReaderFixedNames(final V view, final String... names)
    {
        super(view);
        for (final String name: names)
            attrNames.add(Objects.requireNonNull(name));
    }

    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        if (!attrNames.contains(Objects.requireNonNull(name)))
            throw new NoSuchAttributeException(name);

        return doGetAttributeByName(name);
    }

    protected abstract Object doGetAttributeByName(final String name)
        throws IOException;

    @Nonnull
    @Override
    public final Map<String, Object> getAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        for (final String attrName: attrNames)
            map.put(attrName, getAttributeByName(attrName));

        return Collections.unmodifiableMap(map);
    }
}
