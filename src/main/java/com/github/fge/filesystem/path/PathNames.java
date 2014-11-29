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

package com.github.fge.filesystem.path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@ParametersAreNonnullByDefault
final class PathNames
{
    static final String[] NO_NAMES = new String[0];

    final String root;
    final String[] names;

    @Nonnull
    static PathNames singleton(final String name)
    {
        return new PathNames(null, new String[] { name });
    }

    PathNames(@Nullable final String root, final String[] names)
    {
        this.root = root;
        this.names = names;
    }

    PathNames rootPathName()
    {
        return root == null ? null : new PathNames(root, NO_NAMES);
    }

    @Nullable
    PathNames parent()
    {
        final int length = names.length;
        if (length == 0)
            return null;
        final String[] newNames = length  == 1 ? NO_NAMES
            : Arrays.copyOf(names, length - 1);
        return new PathNames(root, newNames);
    }

    @Nullable
    PathNames lastName()
    {
        final int length = names.length;
        return length == 0 ? null : singleton(names[length - 1]);
    }
}
