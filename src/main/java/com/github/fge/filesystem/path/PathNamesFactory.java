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
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.InvalidPathException;
import java.util.Arrays;

@ParametersAreNonnullByDefault
public abstract class PathNamesFactory
{
    protected static final String[] NO_NAMES = new String[0];

    private final String absolutePrefix;
    private final String separator;

    protected PathNamesFactory(final String absolutePrefix,
        final String separator)
    {
        this.absolutePrefix = absolutePrefix;
        this.separator = separator;
    }

    protected abstract boolean isAbsolutePath(final String path);

    /**
     * Extract raw names from a given path
     *
     * <p>This method will strip any prefix and suffix from the path, then
     * extract the name elements. No check on individual elements are
     * performed.</p>
     *
     * @param path the path
     * @return an array with the raw name elements
     *
     * @see #toNames(String)
     * @see #isValidName(String)
     */
    protected abstract String[] rawNames(final String path);

    protected abstract boolean isValidName(final String name);

    protected abstract boolean isSelf(final String name);

    protected abstract boolean isParent(final String name);

    /**
     * Extract names from, and check the validity of, a given path
     *
     * @param path the path
     * @return an array with this path's name elements
     * @throws InvalidPathException at least one name element is invalid
     *
     * @see #rawNames(String)
     * @see #isValidName(String)
     */
    @Nonnull
    protected final String[] toNames(final String path)
    {
        final String[] ret = rawNames(path);
        for (final String name: ret)
            if (!isValidName(name))
                throw new InvalidPathException(path,
                    "invalid path element: " + name);
        return ret;
    }

    @Nonnull
    protected final String toString(final PathNames pathNames)
    {
        final StringBuilder sb = new StringBuilder();
        if (pathNames.absolute)
            sb.append(absolutePrefix);

        final String[] names = pathNames.names;
        final int len = names.length;
        if (len == 0)
            return sb.toString();

        sb.append(names[0]);

        for (int i = 1; i < len; i++)
            sb.append(separator).append(names[i]);

        return sb.toString();
    }

    @Nonnull
    protected final String[] normalizeNames(final String[] names)
    {
        final String[] ret = new String[names.length];

        int index = 0;
        for (final String name: names) {
            if (isParent(name)) {
                if (index > 0)
                    index--;
                continue;
            }
            if (!isSelf(name))
                ret[index++] = name;
        }

        return index == 0 ? NO_NAMES : Arrays.copyOf(ret, index);
    }
}
