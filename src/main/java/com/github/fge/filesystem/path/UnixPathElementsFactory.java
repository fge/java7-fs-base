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

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public final class UnixPathElementsFactory
    extends PathElementsFactory
{
    private static final Pattern ROOT_PATTERN = Pattern.compile("^/+");
    private static final Pattern TAIL_PATTERN = Pattern.compile("/+$");
    private static final Pattern SPLIT_PATTERN = Pattern.compile("/+");

    private static final PathElements ROOT
        = new PathElements("/", PathElements.NO_NAMES);

    public UnixPathElementsFactory()
    {
        super("", "/", "..");
    }

    @Override
    protected String[] rootAndNames(final String path)
    {
        final String[] ret = new String[2];

        final String tmp = ROOT_PATTERN.matcher(path).replaceFirst("");
        ret[0] = tmp.equals(path) ? null : "/";
        ret[1] = TAIL_PATTERN.matcher(tmp).replaceFirst("");

        return ret;
    }

    @Override
    protected String[] splitNames(final String names)
    {
        if (names.isEmpty())
            return NO_NAMES;
        if (names.indexOf('/') == -1)
            return new String[] { names };
        return SPLIT_PATTERN.split(names);
    }

    @Override
    protected boolean isValidName(final String name)
    {
        if (name.isEmpty())
            return false;
        if (name.indexOf('\0') != -1)
            return false;
        return name.indexOf('/') == -1;
    }

    @Override
    protected boolean isSelf(final String name)
    {
        return ".".equals(name);
    }

    @Override
    protected boolean isParent(final String name)
    {
        return parentToken.equals(name);
    }

    @Override
    protected boolean isAbsolute(final PathElements pathElements)
    {
        return pathElements.root != null;
    }

    @Override
    protected PathElements getRootPathElements()
    {
        return ROOT;
    }
}
