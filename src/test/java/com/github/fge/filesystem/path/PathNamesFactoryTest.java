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

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class PathNamesFactoryTest
{
    private final PathNamesFactory factory = new UnixPathNamesFactory();

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

    @Test(dataProvider = "normalizeData")
    public void normalizingWorks(final String root, final String[] orig,
        final String[] expectedNames)
    {
        final PathNames pathNames = new PathNames(root, orig);
        final PathNames normalized = factory.normalize(pathNames);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(normalized.root).as("normalize() does not change root")
            .isEqualTo(root);
        soft.assertThat(normalized.names).as("name normalization works")
            .isEqualTo(expectedNames);

        soft.assertAll();
    }

    @Test
    public void resolveWorks()
    {
        PathNames first, second, resolved;

        final SoftAssertions soft = new SoftAssertions();

        first = new PathNames(null, stringArray("foo"));
        second = new PathNames("/", stringArray("bar"));

        soft.assertThat(factory.resolve(first, second))
            .as("second itself is returned if absolute")
            .isSameAs(second);

        second = PathNames.EMPTY;

        soft.assertThat(factory.resolve(first, second))
            .as("first is returned if second has no root nor name components")
            .isSameAs(first);

        first = new PathNames(null, stringArray("a", "b"));
        second = new PathNames(null, stringArray("c", "d"));
        resolved = new PathNames(null, stringArray("a", "b", "c", "d"));

        soft.assertThat(factory.resolve(first, second))
            .as("normal resolution works correctly")
            .isEqualTo(resolved);

        first = new PathNames("/", stringArray("a", "."));
        second = new PathNames(null, stringArray("..", "d"));
        resolved = new PathNames("/", stringArray("a", ".", "..", "d"));

        soft.assertThat(factory.resolve(first, second))
            .as("resolution does not normalize")
            .isEqualTo(resolved);

        soft.assertAll();
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