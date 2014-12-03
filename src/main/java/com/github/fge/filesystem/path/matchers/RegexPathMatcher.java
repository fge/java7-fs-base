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

package com.github.fge.filesystem.path.matchers;

import javax.annotation.Nonnull;
import java.nio.file.PathMatcher;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A {@link PathMatcher} implementation using regexes
 */
public final class RegexPathMatcher
    extends PathMatcherBase
{
    private final Pattern pattern;

    public RegexPathMatcher(@Nonnull final String regex)
    {
        /*
         * We need Pattern.DOTALL, since it is legal in many filesystems for
         * \n or \r to appear in path names
         */
        pattern = Pattern.compile(Objects.requireNonNull(regex),
            Pattern.DOTALL);
    }

    @Override
    protected boolean match(final String input)
    {
        return pattern.matcher(input).find();
    }
}
