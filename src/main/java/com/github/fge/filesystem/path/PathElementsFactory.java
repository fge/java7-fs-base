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
 * normalizing them. An instance of (an implementation of) this class is passed
 * to all {@link GenericPath} instances.</p>
 *
 * <p>Implementations have to override the necessary methods to extract the root
 * components and name elements from a string, but also telling whether a name
 * element is valid at all, or represents the current or parent directory (in
 * typical filesystems, those would be testing that the name is either of {@code
 * "."} or {@code ".."}).</p>
 *
 * <p>This package provides an implementation for Unix paths.</p>
 *
 * <p><strong>Important note:</strong> this class adheres as closely as possible
 * to the {@link Path} contract with regards to resolution and relativization;
 * this means that for both of these operations, <strong>normalization
 * is not performed</strong>. You will therefore have to ensure that paths you
 * submit to these methods are normalized.</p>
 */
@ParametersAreNonnullByDefault
public abstract class PathElementsFactory
{
    protected static final String[] NO_NAMES = new String[0];

    private final String rootSeparator;
    private final String separator;
    protected final String parentToken;

    /**
     * Constructor
     *
     * @param rootSeparator the separator to insert between the root component,
     * if any, and the first name element, if any
     * @param separator the separator to insert between two name elements
     * @param parentToken a canonical path token to represent the parent of the
     * current path
     */
    protected PathElementsFactory(final String rootSeparator,
        final String separator, final String parentToken)
    {
        this.rootSeparator = rootSeparator;
        this.separator = separator;
        this.parentToken = parentToken;
    }

    public final String getSeparator()
    {
        return separator;
    }

    /**
     * Split an input path into the root component and all name elements
     *
     * <p>This method returns a two-element string array, where the first
     * element is the root component and the second element is a string with
     * all name elements and any trailing characters (if any) removed.</p>
     *
     * <p>If the path has no {@link Path#getRoot() root}, the first element of
     * the returned array must be {@code null}.</p>
     *
     * @param path the path
     * @return see description
     */
    protected abstract String[] rootAndNames(final String path);

    /**
     * Split a names-only input into individual name elements
     *
     * <p>The input is guaranteed to be well-formed (no root component, no root
     * separator and no trailing characters). The name elements must be in
     * their order of appearance in the input.</p>
     *
     * @param names the input string
     * @return an array of the name elements, in their order of appearance
     */
    protected abstract String[] splitNames(final String names);

    /**
     * Check whether a name element is valid
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
    // TODO: is it necessary? Can we have filesystems with more than one
    // possible self token?
    protected abstract boolean isSelf(final String name);

    /**
     * Check whether a name element represents the parent directory
     *
     * @param name the name to check
     * @return true if the name represents the parent directory
     *
     * @see #normalize(PathElements)
     */
    // TODO: is it necessary? Can we have filesystems with more than one
    // possible parent token?
    protected abstract boolean isParent(final String name);

    /**
     * Check whether a {@link PathElements} represents an absolute path
     *
     * @param pathElements the instance to check
     * @return true if the instance is an absolute path
     *
     * @see Path#isAbsolute()
     */
    protected abstract boolean isAbsolute(final PathElements pathElements);

    protected abstract PathElements getRootPathElements();

    /**
     * Convert an input string into a {@link PathElements}
     *
     * @param path the string to convert
     * @return a new {@link PathElements} instance
     * @throws InvalidPathException one name element is invalid
     *
     * @see #rootAndNames(String)
     * @see #isValidName(String)
     */
    @Nonnull
    public final PathElements toPathElements(final String path)
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
     * Normalize a {@link PathElements}
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
     * Resolve a {@link PathElements} against another
     *
     * <p>This method replicates the contract of {@link Path#resolve(Path)}. In
     * this method, the {@code first} argument is the instance performing the
     * resolution and the {@code second} argument is the method's argument.</p>
     *
     * <ul>
     *     <li>if the second argument is {@link #isAbsolute(PathElements)
     *     absolute}, it is returned;</li>
     *     <li>if the second argument has no name components, the first
     *     argument is returned;</li>
     *     <li>otherwise, resolution is performed by just appending the name
     *     components of the first argument to the second argument.</li>
     * </ul>
     *
     * <p>NOTES:</p>
     *
     * <ul>
     *     <li>as per the original method, <strong>no normalization of any
     *     argument is performed</strong>;</li>
     *     <li>if the second argument is deemed to be absolute but it does not
     *     have a root component, this method throws an {@link
     *     UnsupportedOperationException}.</li>
     * </ul>
     *
     * @param first the path performing the operation
     * @param second the path on which the operation is performed
     * @return the resulting path
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
     * Resolve a {@link PathElements}'s parent against another
     *
     * <p>This method replicates the contract of {@link
     * Path#resolveSibling(Path)}. In this method, the {@code first} argument is
     * the instance performing the resolution and the {@code second} argument is
     * the method's argument.</p>
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
     * @param first the path performing the operation
     * @param second the path on which the operation is performed
     * @return the resulting path
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

    /**
     * Relativize a path against another
     *
     * <p>This mirrors the behaviour of {@link Path#relativize(Path)}; in
     * this method, {@code first} is the path performing the operation and
     * {@code second} is the method's argument.</p>
     *
     * <p>NOTE: in the event that both path elements have root components
     * and they are not equal, an {@link IllegalArgumentException} is thrown
     * unconditionally.</p>
     *
     * @param first the path performing the operation
     * @param second the path on which the operation is performed
     * @return the resulting path
     *
     * @see Path#relativize(Path)
     */
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

        final int minLen = Math.min(firstLen, secondLen);

        /*
         * Start by skipping the common elements at the beginning of both name
         * elements. We save the index since we will have to use it later.
         */
        int srcIndex;

        for (srcIndex = 0; srcIndex < minLen; srcIndex++)
            if (!firstNames[srcIndex].equals(secondNames[srcIndex]))
                break;

        /*
         * The resulting name elements array will always have the length of
         * both the original name arrays minus twice the number of common
         * elements.
         */
        final int newNamesLength = firstLen + secondLen - 2 * srcIndex;

        /*
         * We can immediately return if the new length is 0: this means that
         * both name arrays are exhausted, which in turns means that the paths
         * are the same. In this case, as per the docs, return an empty path.
         */

        if (newNamesLength == 0)
            return PathElements.EMPTY;

        final String[] newNames = new String[newNamesLength];

        /*
         * Where to insert in the new names array
         */
        int dstIndex = 0;

        /*
         * Since all common elements to both name elements have been exhausted,
         * the first step after that is to insert parent tokens into the result
         * array while there are still elements in the first array.
         */
        for (int len = srcIndex; len < firstLen; len++)
            newNames[dstIndex++] = parentToken;

        /*
         * Finally we need to insert all remaining tokens of the second array.
         */
        for (int len = srcIndex; len < secondLen; len++)
            newNames[dstIndex++] = secondNames[len];

        return new PathElements(null, newNames);
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
