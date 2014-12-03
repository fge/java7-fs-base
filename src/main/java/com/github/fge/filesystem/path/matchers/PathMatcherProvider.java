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
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class PathMatcherProvider
{
    private static final MethodHandles.Lookup LOOKUP
        = MethodHandles.publicLookup();
    private static final MethodType CONSTRUCTOR_TYPE
        = MethodType.methodType(void.class, String.class);

    private final Map<String, MethodHandle> handleMap
        = new HashMap<>();

    public PathMatcherProvider()
    {
        registerPathMatcher("glob", GlobPathMatcher.class);
        registerPathMatcher("regex", RegexPathMatcher.class);
    }

    public final PathMatcher getPathMatcher(final String name, final String arg)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(arg);

        final MethodHandle handle = handleMap.get(name);
        if (handle == null)
            throw new UnsupportedOperationException();

        try {
            return (PathMatcher) handle.invokeExact(arg);
        } catch (Throwable throwable) {
            throw new RuntimeException("Unhandled exception", throwable);
        }
    }

    protected final void registerPathMatcher(@Nonnull final String name,
        @Nonnull final Class<? extends PathMatcher> matcherClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(matcherClass);

        final MethodHandle handle;
        final MethodType type;

        try {
            handle = LOOKUP.findConstructor(matcherClass, CONSTRUCTOR_TYPE);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("cannot find constructor", e);
        }

        type = handle.type().changeReturnType(PathMatcher.class);
        handleMap.put(name, handle.asType(type));
    }
}
