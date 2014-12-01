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
import java.util.Arrays;
import java.util.Objects;


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

    @SuppressWarnings("VariableNotUsedInsideIf")
    public final PathElementsAssert hasNullRoot()
    {
        if (actual.root != null)
            failWithMessage("root component is not null\n (is: <%s>)",
                actual.root);
        return this;
    }

    public final PathElementsAssert hasRoot(final String expected)
    {
        if (!Objects.equals(actual.root, expected))
            failWithMessage(
                "root component is not what is expected\n"
                + "expected: <%s>\nactual: <%s>\n",
                expected, actual.root
            );
        return this;
    }

    public final PathElementsAssert hasSameRootAs(final PathElements other)
    {
        if (!Objects.equals(actual.root, other.root))
            failWithMessage(
                "root component is not the same as other\n"
                + "expected: <%s>\nactual  : <%s>\n",
                other.root, actual.root
            );
        return this;
    }

    /*
     * names check
     */

    public final PathElementsAssert hasNoNames()
    {
        if (actual.names.length != 0)
            failWithMessage("names array (%s) is not empty",
                Arrays.toString(actual.names));
        return this;
    }

    public final PathElementsAssert hasNames(final String... expected)
    {
        if (!Arrays.equals(actual.names, expected))
            failWithMessage("names array is not what is expected\n"
                + "expected: <%s>\nactual  : <%s>\n",
                Arrays.toString(expected), Arrays.toString(actual.names));
        return this;
    }

    public final PathElementsAssert hasSameNamesAs(final PathElements other)
    {
        if (!Arrays.equals(actual.names, other.names))
            failWithMessage(
                "names differ from provided elements instance\n"
                + "expected: <%s>\nactual  : <%s>\n",
                Arrays.toString(other.names), Arrays.toString(actual.names));
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
