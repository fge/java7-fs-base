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

import com.github.fge.filesystem.CustomSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.testng.Assert.assertTrue;

public final class PathElementsFactoryTest
{
    private final PathElementsFactory factory = new UnixPathElementsFactory();

    @DataProvider
    public Iterator<Object[]> rootAndNamesData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "", null, "" });
        list.add(new Object[] { "/", "/", "" });
        list.add(new Object[] { "//", "/", "" });
        list.add(new Object[] { "foo/bar", null, "foo/bar" });
        list.add(new Object[] { "foo/bar/", null, "foo/bar" });
        list.add(new Object[] { "foo//bar", null, "foo//bar" });
        list.add(new Object[] { "foo//bar///", null, "foo//bar" });
        list.add(new Object[] { "/foo/bar", "/", "foo/bar" });
        list.add(new Object[] { "/foo/bar/", "/", "foo/bar" });
        list.add(new Object[] { "//foo/bar/", "/", "foo/bar" });
        list.add(new Object[] { "//foo//bar", "/", "foo//bar" });
        list.add(new Object[] { "//foo//bar///", "/", "foo//bar" });

        return list.iterator();
    }

    @Test(dataProvider = "rootAndNamesData")
    public void rooAndNamesSplitsCorrectly(final String path, final String root,
        final String names)
    {
        final String[] ret = factory.rootAndNames(path);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(ret[0]).as("root is correctly calculated")
            .isEqualTo(root);
        soft.assertThat(ret[1]).as("names are correctly extracted")
            .isEqualTo(names);

        soft.assertAll();
    }

    @DataProvider
    public Iterator<Object[]> splitNamesData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "", new String[0] });
        list.add(new Object[] { "a", stringArray("a") });
        list.add(new Object[] { "a/b/c", stringArray("a", "b", "c") });
        list.add(new Object[] { "a//b/c", stringArray("a", "b", "c") });

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "splitNamesData")
    public void splitNamesWorks(final String input, final String[] names)
    {
        assertThat(factory.splitNames(input)).as("names are split correctly")
            .isEqualTo(names);
    }

    @DataProvider
    public Iterator<Object[]> normalizeData()
    {
        final List<Object[]> list = new ArrayList<>();

        final String[] empty = new String[0];

        list.add(new Object[] { null, empty, empty });
        list.add(new Object[] { "/", empty, empty });
        list.add(new Object[] { "/", stringArray("."), empty });
        list.add(new Object[] { "/", stringArray(".", ".."),
            stringArray("..") });
        list.add(new Object[] { null, stringArray("a", ".", "b"),
            stringArray("a", "b") });
        list.add(new Object[] { null, stringArray("a", ".."), empty });
        list.add(new Object[] { null, stringArray("a", "."),
            stringArray("a") });
        list.add(new Object[] { null, stringArray("a", "..", "b"),
            stringArray("b") });
        list.add(new Object[] { null, stringArray("a", "..", "b", ".", "c"),
            stringArray("b", "c") });
        list.add(new Object[] { null, stringArray("..", "a"),
            stringArray("..", "a") });
        list.add(new Object[] { null, stringArray("..", "..", "a"),
            stringArray("..", "..", "a") });
        list.add(new Object[] { null, stringArray("..", "a", ".."),
            stringArray("..") });
        list.add(new Object[] { "null", stringArray("..", "..", ".", ".."),
            stringArray("..", "..", "..") });

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "normalizeData")
    public void normalizingWorks(final String root, final String[] orig,
        final String[] expectedNames)
    {
        final PathElements elements = new PathElements(root, orig);
        final PathElements normalized = factory.normalize(elements);

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

        soft.assertThat(normalized).hasRoot(root).hasNames(expectedNames);

        soft.assertAll();
    }

    @Test
    public void resolveWorks()
    {
        PathElements first, second, resolved;

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

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

        soft.assertAll();
    }

    @Test
    public void resolveSiblingWorks()
    {
        PathElements first, second, resolved;

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

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

        soft.assertAll();
    }

    @Test
    public void relativizingWithDifferentRootsThrowsIAE()
    {
        final PathElements elements1
            = new PathElements("/", PathElements.NO_NAMES);
        final PathElements elements2 = PathElements.EMPTY;

        try {
            factory.relativize(elements1, elements2);
            fail("No exception thrown!");
        } catch (IllegalArgumentException ignored) {
            assertTrue(true);
        }

        try {
            factory.relativize(elements2, elements1);
            fail("No exception thrown!");
        } catch (IllegalArgumentException ignored) {
            assertTrue(true);
        }
    }

    @DataProvider
    public Iterator<Object[]> relativizeData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] {
            "/",
            stringArray("a", "b"),
            stringArray("a", "b"),
            PathElements.NO_NAMES
        });
        list.add(new Object[] {
            null,
            stringArray("a", "b"),
            stringArray("a", "c"),
            stringArray("..", "c")
        });
        list.add(new Object[] {
            "whatever",
            stringArray("a", "b"),
            stringArray("a", "b", "c", "d", "e"),
            stringArray("c", "d", "e")
        });
        list.add(new Object[] {
            "whatever",
            stringArray("a", "b", "c", "d", "e"),
            stringArray("a", "b", "f"),
            stringArray("..", "..", "..", "f")
        });
        list.add(new Object[] {
            "whatever",
            stringArray("a", "b", "f"),
            stringArray("a", "b", "c", "d", "e"),
            stringArray("..", "c", "d", "e")
        });

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "relativizeData")
    public void relativizeGivesExpectedResults(final String root,
        final String[] firstNames, final String[] secondNames,
        final String[] expectedNames)
    {
        final PathElements first = new PathElements(root, firstNames);
        final PathElements second = new PathElements(root, secondNames);
        final PathElements relativized = factory.relativize(first, second);

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

        soft.assertThat(relativized).hasNullRoot().hasNames(expectedNames);

        soft.assertAll();
    }

    @DataProvider
    public Iterator<Object[]> toStringData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { null, PathElements.NO_NAMES, "" });
        list.add(new Object[] { "/", PathElements.NO_NAMES, "/" });
        list.add(new Object[] { null, stringArray("foo"), "foo" });
        list.add(new Object[] { "/", stringArray("foo"), "/foo" });
        list.add(new Object[] { null, stringArray("foo", "bar"), "foo/bar" });
        list.add(new Object[] { "/", stringArray("foo", "bar"), "/foo/bar" });

        return list.iterator();
    }

    @Test(dataProvider = "toStringData")
    public void toStringWorks(final String root, final String[] names,
        final String expected)
    {
        final PathElements elements = new PathElements(root, names);

        assertThat(factory.toString(elements)).isEqualTo(expected);
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