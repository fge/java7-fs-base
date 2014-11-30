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

public final class PathElementsTest
{
    private static final String[] NO_NAMES = new String[0];

    @Test
    public void singletonHasNoRoot()
    {
        final PathElements elements = PathElements.singleton("foo");
        assertThat(elements.root).overridingErrorMessage(
            "singleton PathElements should have null root").isNull();
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
    public void pathElementsWithNoRootReturnsNullRootPath(final String[] names)
    {
        final PathElements elements = new PathElements(null, names);

        assertThat(elements.rootPathElement()).overridingErrorMessage(
            "root path of a PathElements with null root should be null"
        ).isNull();
    }

    @SuppressWarnings({
        "MethodCanBeVariableArityMethod", "ConstantConditions"
    })
    @Test(dataProvider = "variousNameArrays")
    public void pathElementsWithRootRetunsRootOnlyRootPath(final String[] names)
    {
        final String root = "foo";
        final PathElements elements = new PathElements(root, names);
        final PathElements rootElements = elements.rootPathElement();

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(rootElements.root)
            .overridingErrorMessage(".rootPathElement() should keep the root")
            .isSameAs(root);
        soft.assertThat(rootElements.names)
            .overridingErrorMessage(".rootPathElement() should have no names")
            .isEmpty();

        soft.assertAll();
    }

    @Test
    public void pathElementsWithNoNamesHasNullParent()
    {
        final PathElements elements1 = new PathElements(null, NO_NAMES);
        final PathElements elements2 = new PathElements("foo", NO_NAMES);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(elements1.parent()).overridingErrorMessage(
            "a PathElements with no names must have a null parent"
        ).isNull();
        soft.assertThat(elements2.parent()).overridingErrorMessage(
            "a PathElements with no names must have a null parent"
        ).isNull();

        soft.assertAll();
    }

    @Test
    public void singleNamePathElementParentIsCorrect()
    {
        PathElements elements, parent;

        final SoftAssertions soft = new SoftAssertions();

        elements = new PathElements(null, stringArray("foo"));
        parent = elements.parent();

        soft.assertThat(parent)
            .as("path element with single name and no root has null parent")
            .isNull();

        elements = new PathElements("/", stringArray("foo"));
        parent = elements.parent();

        soft.assertThat(parent)
            .as("path element with single name and a root has a parent")
            .isNotNull();

        //noinspection ConstantConditions
        soft.assertThat(parent.root).as("parent preserves root")
            .isSameAs(elements.root);
        soft.assertThat(parent.names).as("parent has no names")
            .isEmpty();

        soft.assertAll();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void pathNameParentHasRelevantNamesAndPreservesRoot()
    {
        final String[] before = stringArray("foo", "bar", "baz");
        final String[] after = stringArray("foo", "bar");

        final PathElements elementsWithRoot = new PathElements("root", before);
        final PathElements elementsWithoutRoot = new PathElements(null, before);

        final SoftAssertions soft = new SoftAssertions();

        PathElements actual, expected;

        actual = elementsWithRoot.parent();
        expected = new PathElements("root", after);

        soft.assertThat(actual.root).as("parent() preserves root")
            .isEqualTo(expected.root);
        soft.assertThat(actual.names).as("parent() has correct names")
            .isEqualTo(expected.names);

        actual = elementsWithoutRoot.parent();
        expected = new PathElements(null, after);

        soft.assertThat(actual.root).as("parent() preserves root")
            .isNull();
        soft.assertThat(actual.names).as("parent() has correct names")
            .isEqualTo(expected.names);

        soft.assertAll();
    }

    @Test
    public void pathNameWithNoNamesHasNoLastName()
    {
        final PathElements elements1 = new PathElements(null, NO_NAMES);
        final PathElements elements2 = new PathElements("foo", NO_NAMES);

        final SoftAssertions soft = new SoftAssertions();

        soft.assertThat(elements1.lastName()).overridingErrorMessage(
            "a PathElements with no names must not have a last name"
        ).isNull();
        soft.assertThat(elements2.parent()).overridingErrorMessage(
            "a PathElements with no names must not have a last name"
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

        PathElements elements, actual;
        String[] expectedNames;

        elements = new PathElements("root", names1);
        actual = elements.lastName();
        expectedNames = stringArray("baz");

        soft.assertThat(actual.root).as("lastName() always has null root")
            .isNull();
        soft.assertThat(actual.names).as("lastName() has correct name elements")
            .isEqualTo(expectedNames);

        elements = new PathElements(null, names2);
        actual = elements.lastName();
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
        final PathElements p1 = PathElements.EMPTY;
        final String[] names = stringArray("foo");
        final PathElements p2 = new PathElements(null, names);
        final PathElements p3 = new PathElements(null, names);
        final PathElements p4 = new PathElements("/", names);
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