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

public final class PathNamesTest
{
    private static final String[] NO_NAMES = new String[0];

    @Test
    public void singletonHasNoRoot()
    {
        final PathNames pathNames = PathNames.singleton("foo");
        assertThat(pathNames.root)
            .overridingErrorMessage("singleton PathNames should have null root")
            .isNull();
    }

    @DataProvider
    public Iterator<Object[]> variousNameArrays()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[]{ NO_NAMES });
        list.add(new Object[] { stringArray("foo") });
        list.add(new Object[] { stringArray("foo", "bar") });
        list.add(new Object[] { stringArray("foo", "bar", "baz") });

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "variousNameArrays")
    public void pathNamesWithNoRootReturnsNullRootPath(final String[] names)
    {
        final PathNames pathNames = new PathNames(null, names);
        assertThat(pathNames.rootPathName()).overridingErrorMessage(
            "root path name of a path name with null root should be null"
        ).isNull();
    }

    @SuppressWarnings({
        "MethodCanBeVariableArityMethod", "ConstantConditions"
    })
    @Test(dataProvider = "variousNameArrays")
    public void pathNamesWithRootRetunsRootOnlyRootPath(final String[] names)
    {
        final String root = "foo";
        final PathNames pathNames = new PathNames(root, names);
        final PathNames rootPath = pathNames.rootPathName();

        final SoftAssertions soft = new SoftAssertions();
        soft.assertThat(rootPath.root)
            .overridingErrorMessage(".rootPathName() should keep the root")
            .isSameAs(root);
        soft.assertThat(rootPath.names)
            .overridingErrorMessage(".rootPathName() should have no names")
            .isEmpty();
        soft.assertAll();
    }

    @Test
    public void pathNameWithNoNamesHasNullParent()
    {
        final PathNames pathNames1 = new PathNames(null, NO_NAMES);
        final PathNames pathNames2 = new PathNames("foo", NO_NAMES);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(pathNames1.parent()).overridingErrorMessage(
            "a PathNames with no names must have a null parent"
        ).isNull();
        soft.assertThat(pathNames2.parent()).overridingErrorMessage(
            "a PathNames with no names must have a null parent"
        ).isNull();
        soft.assertAll();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void pathNameParentHasRelevantNamesAndPreservesRoot()
    {
        final String[] before = stringArray("foo", "bar", "baz");
        final String[] after = stringArray("foo", "bar");

        final PathNames pathNamesWithRoot = new PathNames("root", before);
        final PathNames pathNamesWithoutRoot = new PathNames(null, before);

        final SoftAssertions soft = new SoftAssertions();

        PathNames actual, expected;

        actual = pathNamesWithRoot.parent();
        expected = new PathNames("root", after);

        soft.assertThat(actual.root).as("parent() preserves root")
            .isEqualTo(expected.root);
        soft.assertThat(actual.names).as("parent() has correct names")
            .isEqualTo(expected.names);

        actual = pathNamesWithoutRoot.parent();
        expected = new PathNames(null, after);

        soft.assertThat(actual.root).as("parent() preserves root")
            .isNull();
        soft.assertThat(actual.names).as("parent() has correct names")
            .isEqualTo(expected.names);

        soft.assertAll();
    }

    @Test
    public void pathNameWithNoNamesHasNoLastName()
    {
        final PathNames pathNames1 = new PathNames(null, NO_NAMES);
        final PathNames pathNames2 = new PathNames("foo", NO_NAMES);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(pathNames1.lastName()).overridingErrorMessage(
            "a PathNames with no names must not have a last name").isNull();
        soft.assertThat(pathNames2.parent()).overridingErrorMessage(
            "a PathNames with no names must not have a last name"
        ).isNull();

        soft.assertAll();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void pathNameLastNameWorksAndHasNoRoot()
    {
        final String[] names1 = stringArray("foo", "bar", "baz");
        final String[] names2 = stringArray("foo", "bar");

        final SoftAssertions soft = new SoftAssertions();

        PathNames pathNames, actual;
        String[] expectedNames;

        pathNames = new PathNames("root", names1);
        actual = pathNames.lastName();
        expectedNames = stringArray("baz");

        soft.assertThat(actual.root).as("lastName() always has null root")
            .isNull();
        soft.assertThat(actual.names).as("lastName() has correct name elements")
            .isEqualTo(expectedNames);

        pathNames = new PathNames(null, names2);
        actual = pathNames.lastName();
        expectedNames = stringArray("bar");

        soft.assertThat(actual.root).as("lastName() always has null root")
            .isNull();
        soft.assertThat(actual.names).as("lastName() has correct name elements")
            .isEqualTo(expectedNames);

        soft.assertAll();
    }

    @Test
    public void equalsHashCodeWorks()
    {
        final PathNames p1 = PathNames.EMPTY;
        final String[] names = stringArray("foo");
        final PathNames p2 = new PathNames(null, names);
        final PathNames p3 = new PathNames(null, names);
        final PathNames p4 = new PathNames("/", names);
        final Object o = new Object();

        final SoftAssertions soft = new SoftAssertions();

        //noinspection EqualsWithItself
        soft.assertThat(p1.equals(p1)).isTrue();
        soft.assertThat(p1.hashCode()).isEqualTo(p1.hashCode());

        soft.assertThat(p1.equals(o)).isFalse();
        //noinspection ObjectEqualsNull
        soft.assertThat(p1.equals(null)).isFalse();

        soft.assertThat(p2.equals(p3)).isTrue();
        soft.assertThat(p3.equals(p2)).isTrue();
        soft.assertThat(p3.hashCode()).isEqualTo(p2.hashCode());

        soft.assertThat(p4.equals(p3)).isFalse();

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