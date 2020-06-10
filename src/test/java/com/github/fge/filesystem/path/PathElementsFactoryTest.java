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

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.fge.filesystem.CustomSoftAssertions;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public final class PathElementsFactoryTest
{
    private final PathElementsFactory factory = new UnixPathElementsFactory();

    @RegisterExtension
    final CustomSoftAssertions soft = new CustomSoftAssertions();

    static Stream<Arguments> rootAndNamesData()
    {
        return Stream.of(
            arguments("", null, "" ),
            arguments("/", "/", "" ),
            arguments("//", "/", "" ),
            arguments("foo/bar", null, "foo/bar" ),
            arguments("foo/bar/", null, "foo/bar" ),
            arguments("foo//bar", null, "foo//bar" ),
            arguments("foo//bar///", null, "foo//bar" ),
            arguments("/foo/bar", "/", "foo/bar" ),
            arguments("/foo/bar/", "/", "foo/bar" ),
            arguments("//foo/bar/", "/", "foo/bar" ),
            arguments("//foo//bar", "/", "foo//bar" ),
            arguments("//foo//bar///", "/", "foo//bar")
        );
    }

    @ParameterizedTest
    @MethodSource("rootAndNamesData")
    public void rooAndNamesSplitsCorrectly(final String path, final String root,
        final String names)
    {
        final String[] ret = factory.rootAndNames(path);

        soft.assertThat(ret[0]).as("root is correctly calculated")
            .isEqualTo(root);
        soft.assertThat(ret[1]).as("names are correctly extracted")
            .isEqualTo(names);
    }

    static Stream<Arguments> splitNamesData()
    {
        return Stream.of(
            arguments("", new String[0]),
            arguments("a", stringArray("a")),
            arguments("a/b/c", stringArray("a", "b", "c")),
            arguments("a//b/c", stringArray("a", "b", "c"))
        );
    }

    @ParameterizedTest
    @MethodSource("splitNamesData")
    public void splitNamesWorks(final String input, final String[] names)
    {
        assertArrayEquals(names, factory.splitNames(input), "names are split correctly");
    }

    static Stream<Arguments> normalizeData()
    {
        final String[] empty = new String[0];

        return Stream.of(
            arguments(null, empty, empty ),
            arguments("/", empty, empty ),
            arguments("/", stringArray("."), empty ),
            arguments("/", stringArray(".", ".."), stringArray("..") ),
            arguments(null, stringArray("a", ".", "b"), stringArray("a", "b") ),
            arguments(null, stringArray("a", ".."), empty ),
            arguments(null, stringArray("a", "."), stringArray("a") ),
            arguments(null, stringArray("a", "..", "b"), stringArray("b") ),
            arguments(null, stringArray("a", "..", "b", ".", "c"), stringArray("b", "c") ),
            arguments(null, stringArray("..", "a"), stringArray("..", "a") ),
            arguments(null, stringArray("..", "..", "a"), stringArray("..", "..", "a") ),
            arguments(null, stringArray("..", "a", ".."), stringArray("..") ),
            arguments("null", stringArray("..", "..", ".", ".."), stringArray("..", "..", ".."))
        );
    }

    @ParameterizedTest
    @MethodSource("normalizeData")
    public void normalizingWorks(final String root, final String[] orig,
        final String[] expectedNames)
    {
        final PathElements elements = new PathElements(root, orig);
        final PathElements normalized = factory.normalize(elements);

        soft.assertThat(normalized).hasRoot(root).hasNames(expectedNames);
    }

    @Test
    public void resolveWorks()
    {
        PathElements first, second, resolved;

        first = new PathElements(null, stringArray("foo"));
        second = new PathElements("/", stringArray("bar"));

        soft.assertThat(factory.resolve(first, second))
            .as("second itself is returned if absolute")
            .isSameAs(second);

        second = PathElements.EMPTY;

        soft.assertThat(factory.resolve(first, second))
            .as("first is returned if second has no root nor name components")
            .isSameAs(first);

        first = new PathElements(null, stringArray("a", "b"));
        second = new PathElements(null, stringArray("c", "d"));
        resolved = new PathElements(null, stringArray("a", "b", "c", "d"));

        soft.assertThat(factory.resolve(first, second))
            .hasSameContentsAs(resolved);

        first = new PathElements("/", stringArray("a", "."));
        second = new PathElements(null, stringArray("..", "d"));
        resolved = new PathElements("/", stringArray("a", ".", "..", "d"));

        soft.assertThat(factory.resolve(first, second))
            .hasSameContentsAs(resolved);
    }

    @Test
    public void resolveSiblingWorks()
    {
        PathElements first, second, resolved;

        first = new PathElements(null, stringArray("foo"));
        second = new PathElements(null, new String[0]);
        resolved = factory.resolveSibling(first, second);

        soft.assertThat(resolved)
            .as("if first has no parent, second is returned even if empty")
            .isSameAs(second);

        second = new PathElements(null, stringArray("bar"));
        resolved = factory.resolveSibling(first, second);

        soft.assertThat(resolved)
            .as("if first has no parent, second is returned")
            .isSameAs(second);

        first = new PathElements("/", stringArray("foo", "bar"));
        second = new PathElements("/", stringArray("bar"));
        resolved = factory.resolveSibling(first, second);

        soft.assertThat(resolved)
            .as("if second is absolute, it is returned")
            .isSameAs(second);

        second = new PathElements(null, stringArray("baz"));
        resolved = factory.resolveSibling(first, second);

        soft.assertThat(resolved).hasSameRootAs(first)
            .hasNames("foo", "baz");
    }

    @Test
    public void relativizingWithDifferentRootsThrowsIAE()
    {
        final PathElements elements1
            = new PathElements("/", PathElements.NO_NAMES);
        final PathElements elements2 = PathElements.EMPTY;

        assertThrows(IllegalArgumentException.class, () -> {
            factory.relativize(elements1, elements2);
        }, "No exception thrown!");

        assertThrows(IllegalArgumentException.class, () -> {
            factory.relativize(elements2, elements1);
        }, "No exception thrown!");
    }

    static Stream<Arguments> relativizeData()
    {
        return Stream.of(
            arguments(
                "/",
                stringArray("a", "b"),
                stringArray("a", "b"),
                PathElements.NO_NAMES
            ),
            arguments(
                null,
                stringArray("a", "b"),
                stringArray("a", "c"),
                stringArray("..", "c")
            ),
            arguments(
                "whatever",
                stringArray("a", "b"),
                stringArray("a", "b", "c", "d", "e"),
                stringArray("c", "d", "e")
            ),
            arguments(
                "whatever",
                stringArray("a", "b", "c", "d", "e"),
                stringArray("a", "b", "f"),
                stringArray("..", "..", "..", "f")
            ),
            arguments(
                "whatever",
                stringArray("a", "b", "f"),
                stringArray("a", "b", "c", "d", "e"),
                stringArray("..", "c", "d", "e")
            )
        );
    }

    @ParameterizedTest
    @MethodSource("relativizeData")
    public void relativizeGivesExpectedResults(final String root,
        final String[] firstNames, final String[] secondNames,
        final String[] expectedNames)
    {
        final PathElements first = new PathElements(root, firstNames);
        final PathElements second = new PathElements(root, secondNames);
        final PathElements relativized = factory.relativize(first, second);

        soft.assertThat(relativized).hasNullRoot().hasNames(expectedNames);
    }

    @Test
    public void toUriPathRefusesNonAbsolutePath()
    {
        final PathElements elements = new PathElements(null, stringArray("a"));

        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            factory.toUriPath(null, elements);
        });
        assertEquals("elements not absolute", e.getMessage());
    }

    static Stream<Arguments> toUriPathData()
    {
        return Stream.of(
            arguments(null, stringArray("a", "b"), "/a/b" ),
            arguments("/", stringArray("a", "b"), "/a/b"),
            arguments("/foo", stringArray("a", "b"), "/foo/a/b"),
            arguments("/foo/", stringArray("a", "b"), "/foo/a/b"),
            arguments(null, stringArray("..", "..", "a"), "/a"),
            arguments(null, stringArray("a", ".", "b"), "/a/b")
        );
    }

    @ParameterizedTest
    @MethodSource("toUriPathData")
    public void toUriPathGivesCorrectResult(final String prefix,
        final String[] names, final String expected)
    {
        final PathElements elements = new PathElements("/", names);
        final String actual = factory.toUriPath(prefix, elements);

        assertEquals(expected, actual, "URI path is correctly generated");
    }

    static Stream<Arguments> toStringData()
    {
        return Stream.of(
            arguments(null, PathElements.NO_NAMES, "" ),
            arguments("/", PathElements.NO_NAMES, "/" ),
            arguments(null, stringArray("foo"), "foo" ),
            arguments("/", stringArray("foo"), "/foo" ),
            arguments(null, stringArray("foo", "bar"), "foo/bar" ),
            arguments("/", stringArray("foo", "bar"), "/foo/bar" )
        );
    }

    @ParameterizedTest
    @MethodSource("toStringData")
    public void toStringWorks(final String root, final String[] names,
        final String expected)
    {
        final PathElements elements = new PathElements(root, names);

        assertEquals(expected, factory.toString(elements));
    }

    private static String[] stringArray(final String first,
        final String... other)
    {
        if (other.length == 0)
            return new String[] { first };
        final String[] ret = new String[other.length + 1];
        ret[0] = first;
        System.arraycopy(other, 0, ret, 1, other.length);
        return ret;
    }
}