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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A generic representation of a {@link Path}'s elements
 *
 * <p>The two elements of a path are its root component, if any, and its
 * name elements, if any. Note that the validity of name elements is not
 * checked here: this is the role of a {@link PathElementsFactory} to do so.</p>
 *
 * <p>Also note that theoretically, if a path has a {@link Path#getRoot() root
 * component}, it <em>does not mean</em> that it is {@link Path#isAbsolute()
 * absolute}.</p>
 *
 * <p>You will not generate instances of this class directly; this is up to
 * a {@link PathElementsFactory} to do so.</p>
 *
 * @see GenericPath
 * @see PathElementsFactory
 */
@ParametersAreNonnullByDefault
public final class PathElements
    implements Iterable<PathElements>
{
    /**
     * An empty string array for instances with no elements
     */
    static final String[] NO_NAMES = new String[0];

    /**
     * An empty instance (no root, no names)
     */
    static final PathElements EMPTY = new PathElements(null, NO_NAMES);

    /**
     * The root component of this path
     */
    final String root;

    /**
     * The name components of this path
     */
    final String[] names;


    /**
     * A {@link PathElements} consisting of a single name, with no root
     *
     * @param name the name
     * @return a single-name, no root instance
     */
    @Nonnull
    static PathElements singleton(final String name)
    {
        return new PathElements(null, new String[] { name });
    }

    /**
     * Constructor
     *
     * <p>Note that the names array is <em>not</em> copied; it is up to the
     * caller to ensure the safety of using this array.</p>
     *
     * @param root the root component (may be null)
     * @param names the name elements
     */
    @SuppressWarnings("MethodCanBeVariableArityMethod")
    PathElements(@Nullable final String root, final String[] names)
    {
        this.root = root;
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.names = names;
    }

    /**
     * Return the root PathElements of this instance (null if root is null)
     *
     * @return see description
     *
     * @see Path#getRoot()
     */
    @Nullable
    PathElements rootPathElement()
    {
        return root == null ? null : new PathElements(root, NO_NAMES);
    }

    /**
     * Return the parent PathElements of this instance
     *
     * <p>If this instance has no name elements, {@code null} is returned.</p>
     *
     * <p>If this instance has only one name element and no root, {@code null}
     * is returned.</p>
     *
     * <p>Otherwise a new instance is returned with all name elements except for
     * the last one.</p>
     *
     * <p>The root component is preserved.</p>
     *
     * @return see description
     *
     * @see Path#getParent()
     */
    @Nullable
    PathElements parent()
    {
        final int length = names.length;
        if (length == 0)
            return null;
        if (length == 1 && root == null)
            return null;
        final String[] newNames = length  == 1 ? NO_NAMES
            : Arrays.copyOf(names, length - 1);
        return new PathElements(root, newNames);
    }

    /**
     * Return a PathElements with only the last name element
     *
     * <p>If this PathElements has no names, {@code null} is returned.</p>
     *
     * @return see description
     *
     * @see Path#getFileName()
     */
    @Nullable
    PathElements lastName()
    {
        final int length = names.length;
        return length == 0 ? null : singleton(names[length - 1]);
    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    @SuppressWarnings({
        "AnonymousInnerClassWithTooManyMethods",
        "OverlyComplexAnonymousInnerClass"
    })
    @Override
    public Iterator<PathElements> iterator()
    {
        return new Iterator<PathElements>()
        {
            int index = 0;

            @Override
            public boolean hasNext()
            {
                return index < names.length;
            }

            @Override
            public PathElements next()
            {
                if (!hasNext())
                    throw new NoSuchElementException();
                return singleton(names[index++]);
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public int hashCode()
    {
        return 31 * Objects.hashCode(root) + Arrays.hashCode(names);
    }

    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final PathElements other = (PathElements) obj;
        return Objects.equals(root, other.root)
            && Arrays.equals(names, other.names);
    }
}
