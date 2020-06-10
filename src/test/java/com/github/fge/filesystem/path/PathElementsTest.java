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

import static com.github.fge.filesystem.path.PathElementsAssert.assertElements;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public final class PathElementsTest
{
    @RegisterExtension
    final CustomSoftAssertions soft = new CustomSoftAssertions();

    private static final String[] NO_NAMES = new String[0];

    @Test
    public void singletonHasNoRoot()
    {
        final PathElements elements = PathElements.singleton("foo");
        assertElements(elements).hasNullRoot();
    }

    static Stream<Arguments> variousNameArrays()
    {
        return Stream.of(
            arguments(new Object[] { NO_NAMES }),
            arguments(new Object[] { stringArray("foo") }),
            arguments(new Object[] { stringArray("foo", "bar") }),
            arguments(new Object[] { stringArray("foo", "bar", "baz") })
        );
    }

    @ParameterizedTest
    @MethodSource("variousNameArrays")
    public void pathElementsWithNoRootReturnsNullRootPath(final String[] names)
    {
        final PathElements elements = new PathElements(null, names);

        assertElements(elements).hasNullRoot();
    }

    @ParameterizedTest
    @MethodSource("variousNameArrays")
    public void pathElementsWithRootRetunsRootOnlyRootPath(final String[] names)
    {
        final String root = "foo";
        final PathElements elements = new PathElements(root, names);
        final PathElements rootElements = elements.rootPathElement();

        soft.assertThat(rootElements).hasRoot(root).hasNoNames();
    }

    @Test
    public void pathElementsWithNoNamesHasNullParent()
    {
        final PathElements elements1 = new PathElements(null, NO_NAMES);
        final PathElements elements2 = new PathElements("foo", NO_NAMES);

        soft.assertThat(elements1.parent()).as(
            "a PathElements with no names must have a null parent").isNull();
        soft.assertThat(elements2.parent()).as(
            "a PathElements with no names must have a null parent").isNull();
    }

    @Test
    public void singleNamePathElementParentIsCorrect()
    {
        PathElements elements, parent;

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

        soft.assertThat(parent).hasSameRootAs(elements).hasNoNames();
    }

    @Test
    public void pathNameParentHasRelevantNamesAndPreservesRoot()
    {
        final String[] before = stringArray("foo", "bar", "baz");
        final String[] after = stringArray("foo", "bar");

        final PathElements elementsWithRoot = new PathElements("root", before);
        final PathElements elementsWithoutRoot = new PathElements(null, before);

        PathElements actual, expected;

        actual = elementsWithRoot.parent();
        expected = new PathElements("root", after);

        soft.assertThat(actual).hasSameContentsAs(expected);

        actual = elementsWithoutRoot.parent();
        expected = new PathElements(null, after);

        soft.assertThat(actual).hasSameContentsAs(expected);
    }

    @Test
    public void pathNameWithNoNamesHasNoLastName()
    {
        final PathElements elements1 = new PathElements(null, NO_NAMES);
        final PathElements elements2 = new PathElements("foo", NO_NAMES);

        soft.assertThat(elements1.lastName()).overridingErrorMessage(
            "a PathElements with no names must not have a last name"
        ).isNull();
        soft.assertThat(elements2.parent()).overridingErrorMessage(
            "a PathElements with no names must not have a last name"
        ).isNull();
    }

    @Test
    public void pathNameLastNameWorksAndHasNoRoot()
    {
        final String[] names1 = stringArray("foo", "bar", "baz");
        final String[] names2 = stringArray("foo", "bar");

        PathElements elements, actual;

        elements = new PathElements("root", names1);
        actual = elements.lastName();

        soft.assertThat(actual).hasNullRoot().hasNames("baz");

        elements = new PathElements(null, names2);
        actual = elements.lastName();

        soft.assertThat(actual).hasNullRoot().hasNames("bar");
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