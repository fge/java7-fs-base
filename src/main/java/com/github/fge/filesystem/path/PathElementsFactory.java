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
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract factory for {@link PathElements} instances
 *
 * <p>This class is in charge of all the heavy {@link PathElements} operations:
 * creating them from input strings, but also resolving, relativizing and
 * normalizing them.</p>
 *
 * <p>Implementations have to override the necessary methods to extract the root
 * components and name elements from a string, but also telling whether a name
 * element is valid at all, or represents the current or parent directory (in
 * typical filesystems, those would be testing that the name is either of {@code
 * "."} or {@code ".."}).</p>
 *
 * <p>This package provides an implementation for Unix paths.</p>
 */
@ParametersAreNonnullByDefault
public abstract class PathElementsFactory
{
    protected static final String[] NO_NAMES = new String[0];

    private final String rootSeparator;
    private final String separator;

    /**
     * Constructor
     *
     * @param rootSeparator the separator to insert between the root component,
     * if any, and the first name element, if any
     * @param separator the separator to insert between two name elements
     */
    protected PathElementsFactory(final String rootSeparator,
        final String separator)
    {
        this.rootSeparator = rootSeparator;
        this.separator = separator;
    }

    /**
     * Split an input path into the root component and all name elements
     *
     * <p>This method returns a two-element string array, where the first
     * element is the root component and the second element is all name
     * elements.</p>
     *
     * <p>This method also removes all trailing characters from the name
     * elements, if any. If the path has no root, the first element of the
     * returned array must be {@code null}.</p>
     *
     * @param path the path
     * @return see description
     */
    protected abstract String[] rootAndNames(final String path);

    /**
     * Split a names-only input into the individual name components
     *
     * <p>The input is guaranteed to be well-formed (no root component, no
     * trailing characters). The name components must be in their order of
     * appearance in the input.</p>
     *
     * @param namesOnly the input string
     * @return an array of the different name components
     */
    protected abstract String[] splitNames(final String namesOnly);

    /**
     * Check whether a name element is valid for that factory
     *
     * @param name the name to check
     * @return true if the name is valid
     */
    protected abstract boolean isValidName(final String name);

    /**
     * Check whether a name element represents the current directory
     *
     * @param name the name to check
     * @return true if the name represents the current directory
     *
     * @see #normalize(PathElements)
     */
    protected abstract boolean isSelf(final String name);

    /**
     * Check whether a name element represents the parent directory
     *
     * @param name the name to check
     * @return true if the name represents the parent directory
     *
     * @see #normalize(PathElements)
     */
    protected abstract boolean isParent(final String name);

    /**
     * Check whether a {@link PathElements} instance represents an absolute path
     *
     * @param pathElements the instance to check
     * @return true if the instance is an absolute path
     *
     * @see Path#isAbsolute()
     */
    protected abstract boolean isAbsolute(final PathElements pathElements);

    /**
     * Convert an input string into a {@link PathElements} instance
     *
     * @param path the string to convert
     * @return a new {@link PathElements} instance
     * @throws InvalidPathException one name element is wrong
     *
     * @see #rootAndNames(String)
     * @see #isValidName(String)
     */
    @Nonnull
    protected final PathElements toPathElements(final String path)
    {
        final String[] rootAndNames = rootAndNames(path);
        final String root = rootAndNames[0];
        final String namesOnly = rootAndNames[1];

        final String[] names = splitNames(namesOnly);

        for (final String name: names)
            if (!isValidName(name))
                throw new InvalidPathException(path,
                    "invalid path element: " + name);

        return new PathElements(root, names);
    }

    /**
     * Normalize a {@link PathElements} instance
     *
     * @param elements the instance to normalize
     * @return a new, normalized instance
     *
     * @see #isSelf(String)
     * @see #isParent(String)
     * @see Path#normalize()
     */
    @Nonnull
    protected final PathElements normalize(final PathElements elements)
    {
        final String[] names = elements.names;
        final int length = names.length;
        final String[] newNames = new String[length];

        int dstIndex = 0;
        boolean seenRegularName = false;

        for (final String name: names) {
            /*
             * Just skip self names
             */
            if (isSelf(name))
                continue;
            /*
             * Copy over regular names, and say that we have seen such a token
             */
            if (!isParent(name)) {
                newNames[dstIndex++] = name;
                seenRegularName = true;
                continue;
            }
            /*
             * Parent token... If we have seen a regular token already _and_
             * the destination array contains at least one element, decrease
             * the destination index; otherwise copy it into the destination
             * array.
             */
            if (seenRegularName && dstIndex > 0)
                dstIndex--;
            else
                newNames[dstIndex++] = name;
        }

        return new PathElements(elements.root,
            dstIndex == 0 ? NO_NAMES : Arrays.copyOf(newNames, dstIndex));
    }

    /**
     * Resolve a {@link PathElements} instance against another
     *
     * <p>This method mimicks the {@link Path#resolve(Path) equivalent operation
     * from {@code Path}}, with the first argument being the path to be
     * resolved against and the second one being the argument of the method:</p>
     *
     * <ul>
     *     <li>if the second argument is {@link #isAbsolute(PathElements)
     *     absolute}, it is returned;</li>
     *     <li>if the second argument has no name components, the first
     *     argument is returned;</li>
     *     <li>otherwise, resolution is performed by just appending the name
     *     components of the first argument to the second argument; no
     *     normalization is performed.</li>
     * </ul>
     *
     * <p>NOTE: this method throws an {@link UnsupportedOperationException} if
     * the second argument is not absolute but has a root component.</p>
     *
     * @param first the path to resolve against
     * @param second the path to resolve with
     * @return the resolved path; see description
     *
     * @see Path#resolve(Path)
     */
    @Nonnull
    protected final PathElements resolve(final PathElements first,
        final PathElements second)
    {
        if (isAbsolute(second))
            return second;

        //noinspection VariableNotUsedInsideIf
        if (second.root != null)
            throw new UnsupportedOperationException();

        final String[] firstNames = first.names;
        final String[] secondNames = second.names;
        final int firstLen = firstNames.length;
        final int secondLen = secondNames.length;

        if (secondLen == 0)
            return first;

        final String[] newNames
            = Arrays.copyOf(firstNames, firstLen + secondLen);
        System.arraycopy(secondNames, 0, newNames, firstLen, secondLen);

        return new PathElements(first.root, newNames);
    }

    /**
     * Resolve a {@link PathElements} against the sibling of another
     *
     * <p>This method performs the same as {@link Path#resolveSibling(Path) the
     * equivalent operation of {@code Path}}, with the first argument being
     * the path to resolve against and the second being the path to resolve.</p>
     *
     * <p>The rules are the same as the equivalent {@code Path} method:</p>
     *
     * <ul>
     *     <li>if the second argument is absolute, it is returned;</li>
     *     <li>if the first argument has no parent, the second is returned;</li>
     *     <li>if the second argument is an empty path, the first argument's
     *     {@link PathElements#parent() parent} is returned, or the {@link
     *     PathElements#EMPTY empty path} if parent is null.</li>
     * </ul>
     *
     * @param first path to resolve against
     * @param second resolved path
     * @return the result path
     */
    @Nonnull
    protected final PathElements resolveSibling(final PathElements first,
        final PathElements second)
    {
        if (isAbsolute(second))
            return second;

        //noinspection VariableNotUsedInsideIf
        if (second.root != null)
            throw new UnsupportedOperationException();

        final PathElements firstParent = first.parent();

        /*
         * Note: agrees with native paths
         */
        if (firstParent == null)
            return second;

        return resolve(firstParent, second);
    }

    @Nonnull
    protected final PathElements relativize(final PathElements first,
        final PathElements second)
    {
        /*
         * FIXME: javadoc says that if both have a root component then it is
         * implementation dependent; we throw IAE unconditionally here. As
         * Objects.equals() accounts for null, we are OK.
         */
        if (!Objects.equals(first.root, second.root))
            throw new IllegalArgumentException();

        final String[] firstNames = first.names;
        final String[] secondNames = second.names;

        final int firstLen = firstNames.length;
        final int secondLen = secondNames.length;

        /*
         * The resulting names array will have at most the added length of both
         * name arrays.
         */
        final String[] newNames = new String[firstLen + secondLen];

        int insertionIndex = 0;

        final int minLen = Math.min(firstLen, secondLen);

        int restartIndex;
        String name;

        /*
         * Start by skipping the common elements at the beginning
         */
        for (restartIndex = 0; restartIndex < minLen; restartIndex++) {
            name = firstNames[restartIndex];
            if (!name.equals(secondNames[restartIndex]))
                break;
        }

        /*
         * OK, no more common elements. This means we need to insert parent
         * tokens into the result array while there are still elements in the
         * first array.
         */
        // TODO: unhardcode parent!!
        for (int len = restartIndex; len < firstLen; len++)
            newNames[insertionIndex++] = "..";

        /*
         * Finally we need to insert all remaining tokens of the second array.
         */
        for (int len = restartIndex; len < secondLen; len++)
            newNames[insertionIndex++] = secondNames[len];

        return insertionIndex == 0
            ? PathElements.EMPTY
            : new PathElements(null, Arrays.copyOf(newNames, insertionIndex));
    }

    /**
     * Return a string representation of a {@link PathElements} instance
     *
     * <p>{@link PathElements} does not define {@code .toString()}, as the
     * string representation of a path is highly system dependent.</p>
     *
     * @param elements the instance
     * @return a string representation
     */
    @Nonnull
    protected final String toString(final PathElements elements)
    {
        final StringBuilder sb = new StringBuilder();

        final boolean hasRoot = elements.root != null;
        final String[] names = elements.names;
        final int len = names.length;

        if (hasRoot)
            sb.append(elements.root);

        if (len == 0)
            return sb.toString();

        if (hasRoot)
            sb.append(rootSeparator);

        sb.append(names[0]);

        for (int i = 1; i < len; i++)
            sb.append(separator).append(names[i]);

        return sb.toString();
    }
}
