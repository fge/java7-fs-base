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

import org.assertj.core.api.ObjectAssert;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
// cannot be final, see CustomSoftAssertions
public class PathElementsAssert
    extends ObjectAssert<PathElements>
{
    public PathElementsAssert(final PathElements actual)
    {
        super(actual);
    }

    public static PathElementsAssert assertElements(
        final PathElements actual)
    {
        return new PathElementsAssert(actual);
    }

    /*
     * root checks
     */

    public final PathElementsAssert hasNullRoot()
    {
        assertThat(actual.root).as("root component should be null").isNull();
        return this;
    }

    public final PathElementsAssert hasRoot(final String expected)
    {
        assertThat(actual.root).as("root component is correct")
            .isEqualTo(expected);
        return this;
    }

    public final PathElementsAssert hasSameRootAs(final PathElements other)
    {
        assertThat(actual.root).as("root component should be same as other")
            .isSameAs(other.root);
        return this;
    }

    /*
     * names check
     */

    public final PathElementsAssert hasNoNames()
    {
        assertThat(actual.names).as("should not have any name elements")
            .isEmpty();
        return this;
    }

    public final PathElementsAssert hasNames(final String... expected)
    {
        assertThat(actual.names).as("names should match")
            .containsExactly(expected);
        return this;
    }

    public final PathElementsAssert hasSameNamesAs(final PathElements other)
    {
        assertThat(actual.names).as("names should be same as other")
            .containsExactly(other.names);
        return this;
    }

    /*
     * PathElements check
     */

    public final PathElementsAssert hasSameContentsAs(final PathElements other)
    {
        return hasSameRootAs(other).hasSameNamesAs(other);
    }
}
