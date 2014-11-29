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

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
final class PathNames
{
    final boolean absolute;
    final String[] names;

    static PathNames singleton(final String name)
    {
        return new PathNames(false, new String[] { name });
    }

    PathNames(final boolean absolute, final String[] names)
    {
        this.absolute = absolute;
        this.names = names;
    }

    @Nullable
    PathNames lastName()
    {
        final int length = names.length;
        return length == 0 ? null : singleton(names[length - 1]);
    }
}
