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

package com.github.fge.filesystem.attributes.read;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
 * NOTE: when we get there we are guaranteed that all attributes are supported
 */
@ParametersAreNonnullByDefault
public abstract class FileAttributesReader<A>
{
    protected final A attributes;
    protected final Set<String> supported = new HashSet<>();

    protected FileAttributesReader(final A attributes)
    {
        this.attributes = Objects.requireNonNull(attributes);
    }

    public final Set<String> getSupported()
    {
        return Collections.unmodifiableSet(supported);
    }

    @Nullable
    public abstract Object readAttribute(final String name);

    @Nonnull
    public final Map<String, Object> readAttributes(final Set<String> names)
    {
        final Map<String, Object> ret = new HashMap<>();
        for (final String name: Objects.requireNonNull(names))
            ret.put(Objects.requireNonNull(name), readAttribute(name));

        return ret;
    }
}
