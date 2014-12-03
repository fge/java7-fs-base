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
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;

/**
 * A base implementation of {@link PathMatcher} which delegates matching to a
 * path's string representation
 *
 * @see FileSystem#getPathMatcher(String)
 */
public abstract class PathMatcherBase
    implements PathMatcher
{
    protected abstract boolean match(final String input);

    /**
     * Tells if given path matches this matcher's pattern.
     *
     * @param path the path to match
     * @return {@code true} if, and only if, the path matches this
     * matcher's pattern
     */
    @Override
    public final boolean matches(@Nonnull final Path path)
    {
        return match(Objects.requireNonNull(path).toString());
    }
}
