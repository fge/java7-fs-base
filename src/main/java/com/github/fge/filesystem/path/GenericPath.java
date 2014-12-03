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
import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.net.URI;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderMismatchException;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * A generic {@link Path} implementation
 *
 * <p>Most of the heavy lifting of path manipulation (resolution, parent etc)
 * is delegated to the {@link PathElementsFactory} provided as an argument to
 * the constructor, which is why this class can be made {@code final}.</p>
 *
 * <p>You won't want to create instances of this class directly; use {@link
 * FileSystem#getPath(String, String...)} instead.</p>
 *
 * @see PathElementsFactory
 * @see PathElements
 */
@ParametersAreNonnullByDefault
public final class GenericPath
    implements Path
{
    private final FileSystem fs;

    private final PathElementsFactory factory;
    // visible for testing
    final PathElements elements;
    private final String asString;

    /**
     * Constructor
     *
     * @param fs the file system this path is issued from
     * @param factory the path elements factory
     * @param elements the path elements
     */
    public GenericPath(final FileSystem fs, final PathElementsFactory factory,
        final PathElements elements)
    {
        this.fs = Objects.requireNonNull(fs);
        this.factory = Objects.requireNonNull(factory);
        this.elements = Objects.requireNonNull(elements);
        asString = factory.toString(elements);
    }

    /**
     * Returns the file system that created this object.
     *
     * @return the file system that created this object
     */
    @Override
    public FileSystem getFileSystem()
    {
        return fs;
    }

    /**
     * Tells whether or not this path is absolute.
     * <p> An absolute path is complete in that it doesn't need to be combined
     * with other path information in order to locate a file.
     *
     * @return {@code true} if, and only if, this path is absolute
     *
     * @see PathElementsFactory#isAbsolute(PathElements)
     */
    @Override
    public boolean isAbsolute()
    {
        return factory.isAbsolute(elements);
    }

    /**
     * Returns the root component of this path as a {@code Path} object,
     * or {@code null} if this path does not have a root component.
     *
     * @return a path representing the root component of this path,
     * or {@code null}
     *
     * @see PathElements#rootPathElement()
     */
    @Override
    public Path getRoot()
    {
        final PathElements newElements = elements.rootPathElement();
        return newElements == null ? null
            : new GenericPath(fs, factory, newElements);
    }

    /**
     * Returns the name of the file or directory denoted by this path as a
     * {@code Path} object. The file name is the <em>farthest</em> element from
     * the root in the directory hierarchy.
     *
     * @return a path representing the name of the file or directory, or
     * {@code null} if this path has zero elements
     *
     * @see PathElements#lastName()
     */
    @Override
    public Path getFileName()
    {
        final PathElements names = elements.lastName();
        return names == null ? null : new GenericPath(fs, factory, names);
    }

    /**
     * Returns the <em>parent path</em>, or {@code null} if this path does not
     * have a parent.
     * <p> The parent of this path object consists of this path's root
     * component, if any, and each element in the path except for the
     * <em>farthest</em> from the root in the directory hierarchy. This method
     * does not access the file system; the path or its parent may not exist.
     * Furthermore, this method does not eliminate special names such as "."
     * and ".." that may be used in some implementations. On UNIX for example,
     * the parent of "{@code /a/b/c}" is "{@code /a/b}", and the parent of
     * {@code "x/y/.}" is "{@code x/y}". This method may be used with the {@link
     * #normalize normalize} method, to eliminate redundant names, for cases
     * where
     * <em>shell-like</em> navigation is required.
     * <p> If this path has one or more elements, and no root component, then
     * this method is equivalent to evaluating the expression:
     * <blockquote><pre>
     * subpath(0,&nbsp;getNameCount()-1);
     * </pre></blockquote>
     *
     * @return a path representing the path's parent
     */
    @Override
    public Path getParent()
    {
        final PathElements newNames = elements.parent();
        return newNames == null ? null : new GenericPath(fs, factory, newNames);
    }

    /**
     * Returns the number of name elements in the path.
     *
     * @return the number of elements in the path, or {@code 0} if this path
     * only represents a root component
     */
    @Override
    public int getNameCount()
    {
        return elements.names.length;
    }

    /**
     * Returns a name element of this path as a {@code Path} object.
     * <p> The {@code index} parameter is the index of the name element to
     * return.
     * The element that is <em>closest</em> to the root in the directory
     * hierarchy
     * has index {@code 0}. The element that is <em>farthest</em> from the root
     * has index {@link #getNameCount count}{@code -1}.
     *
     * @param index the index of the element
     * @return the name element
     *
     * @throws IllegalArgumentException if {@code index} is negative, {@code
     * index} is greater than or
     * equal to the number of elements, or this path has zero name
     * elements
     */
    @Override
    public Path getName(final int index)
    {
        final String name;

        //noinspection ProhibitedExceptionCaught
        try {
            name = elements.names[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("illegal index " + index, e);
        }

        return new GenericPath(fs, factory, PathElements.singleton(name));
    }

    /**
     * Returns a relative {@code Path} that is a subsequence of the name
     * elements of this path.
     * <p> The {@code beginIndex} and {@code endIndex} parameters specify the
     * subsequence of name elements. The name that is <em>closest</em> to the
     * root
     * in the directory hierarchy has index {@code 0}. The name that is
     * <em>farthest</em> from the root has index {@link #getNameCount
     * count}{@code -1}. The returned {@code Path} object has the name elements
     * that begin at {@code beginIndex} and extend to the element at index
     * {@code
     * endIndex-1}.
     *
     * @param beginIndex the index of the first element, inclusive
     * @param endIndex the index of the last element, exclusive
     * @return a new {@code Path} object that is a subsequence of the name
     * elements in this {@code Path}
     *
     * @throws IllegalArgumentException if {@code beginIndex} is negative, or
     * greater than or equal to
     * the number of elements. If {@code endIndex} is less than or
     * equal to {@code beginIndex}, or larger than the number of elements.
     */
    @Override
    public Path subpath(final int beginIndex, final int endIndex)
    {
        final String[] names;

        //noinspection ProhibitedExceptionCaught
        try {
            names = Arrays.copyOfRange(elements.names, beginIndex, endIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("invalid begin and/or end index",
                e);
        }

        // The result never has a root
        final PathElements newNames = new PathElements(null, names);
        return new GenericPath(fs, factory, newNames);
    }

    /**
     * Tests if this path starts with the given path.
     * <p> This path <em>starts</em> with the given path if this path's root
     * component <em>starts</em> with the root component of the given path,
     * and this path starts with the same name elements as the given path.
     * If the given path has more name elements than this path then {@code
     * false}
     * is returned.
     * <p> Whether or not the root component of this path starts with the root
     * component of the given path is file system specific. If this path does
     * not have a root component and the given path has a root component then
     * this path does not start with the given path.
     * <p> If the given path is associated with a different {@code FileSystem}
     * to this path then {@code false} is returned.
     *
     * @param other the given path
     * @return {@code true} if this path starts with the given path; otherwise
     * {@code false}
     */
    @Override
    public boolean startsWith(final Path other)
    {
        if (!fs.equals(other.getFileSystem()))
            return false;

        final PathElements otherNames = ((GenericPath) other).elements;
        if (!Objects.equals(elements.root, otherNames.root))
            return false;
        final int len = otherNames.names.length;
        if (len > elements.names.length)
            return false;
        for (int i = 0; i < len; i++)
            if (!elements.names[i].equals(otherNames.names[i]))
                return false;
        return true;
    }

    /**
     * Tests if this path starts with a {@code Path}, constructed by converting
     * the given path string, in exactly the manner specified by the {@link
     * #startsWith(Path) startsWith(Path)} method. On UNIX for example, the path
     * "{@code foo/bar}" starts with "{@code foo}" and "{@code foo/bar}". It
     * does not start with "{@code f}" or "{@code fo}".
     *
     * @param other the given path string
     * @return {@code true} if this path starts with the given path; otherwise
     * {@code false}
     *
     * @throws InvalidPathException If the path string cannot be converted to
     * a Path.
     */
    @Override
    public boolean startsWith(final String other)
    {
        final Path otherPath
            = new GenericPath(fs, factory, factory.toPathElements(other));
        return startsWith(otherPath);
    }

    /**
     * Tests if this path ends with the given path.
     * <p> If the given path has <em>N</em> elements, and no root component,
     * and this path has <em>N</em> or more elements, then this path ends with
     * the given path if the last <em>N</em> elements of each path, starting at
     * the element farthest from the root, are equal.
     * <p> If the given path has a root component then this path ends with the
     * given path if the root component of this path <em>ends with</em> the root
     * component of the given path, and the corresponding elements of both paths
     * are equal. Whether or not the root component of this path ends with the
     * root component of the given path is file system specific. If this path
     * does not have a root component and the given path has a root component
     * then this path does not end with the given path.
     * <p> If the given path is associated with a different {@code FileSystem}
     * to this path then {@code false} is returned.
     *
     * @param other the given path
     * @return {@code true} if this path ends with the given path; otherwise
     * {@code false}
     */
    @Override
    public boolean endsWith(final Path other)
    {
        if (!fs.equals(other.getFileSystem()))
            return false;

        final PathElements otherElements = ((GenericPath) other).elements;

        //noinspection VariableNotUsedInsideIf
        if (otherElements.root != null)
            return false;

        final String[] names = elements.names;
        final int length = names.length;
        final String[] otherNames = otherElements.names;
        final int otherLength = otherNames.length;

        if (length < otherLength)
            return false;

        for (int i = 0; i < otherLength; i++)
            if (!names[length - i].equals(otherNames[otherLength - i]))
                return false;

        return true;
    }

    /**
     * Tests if this path ends with a {@code Path}, constructed by converting
     * the given path string, in exactly the manner specified by the {@link
     * #endsWith(Path) endsWith(Path)} method. On UNIX for example, the path
     * "{@code foo/bar}" ends with "{@code foo/bar}" and "{@code bar}". It does
     * not end with "{@code r}" or "{@code /bar}". Note that trailing separators
     * are not taken into account, and so invoking this method on the {@code
     * Path}"{@code foo/bar}" with the {@code String} "{@code bar/}" returns
     * {@code true}.
     *
     * @param other the given path string
     * @return {@code true} if this path starts with the given path; otherwise
     * {@code false}
     *
     * @throws InvalidPathException If the path string cannot be converted to
     * a Path.
     */
    @Override
    public boolean endsWith(final String other)
    {
        final GenericPath otherPath
            = new GenericPath(fs, factory, factory.toPathElements(other));
        return endsWith(otherPath);
    }

    /**
     * Returns a path that is this path with redundant name elements eliminated.
     * <p> The precise definition of this method is implementation dependent but
     * in general it derives from this path, a path that does not contain
     * <em>redundant</em> name elements. In many file systems, the "{@code .}"
     * and "{@code ..}" are special names used to indicate the current directory
     * and parent directory. In such file systems all occurrences of "{@code .}"
     * are considered redundant. If a "{@code ..}" is preceded by a
     * non-"{@code ..}" name then both names are considered redundant (the
     * process to identify such names is repeated until is it no longer
     * applicable).
     * <p> This method does not access the file system; the path may not locate
     * a file that exists. Eliminating "{@code ..}" and a preceding name from a
     * path may result in the path that locates a different file than the
     * original
     * path. This can arise when the preceding name is a symbolic link.
     *
     * @return the resulting path or this path if it does not contain
     * redundant name elements; an empty path is returned if this path
     * does have a root component and all name elements are redundant
     *
     * @see #getParent
     * @see #toRealPath
     */
    @Override
    public Path normalize()
    {
        final PathElements normalized = factory.normalize(elements);
        return elements.equals(normalized) ? this
            : new GenericPath(fs, factory, normalized);
    }

    /**
     * Resolve the given path against this path.
     * <p> If the {@code other} parameter is an {@link #isAbsolute() absolute}
     * path then this method trivially returns {@code other}. If {@code other}
     * is an <i>empty path</i> then this method trivially returns this path.
     * Otherwise this method considers this path to be a directory and resolves
     * the given path against this path. In the simplest case, the given path
     * does not have a {@link #getRoot root} component, in which case this
     * method
     * <em>joins</em> the given path to this path and returns a resulting path
     * that {@link #endsWith ends} with the given path. Where the given path has
     * a root component then resolution is highly implementation dependent and
     * therefore unspecified.
     *
     * @param other the path to resolve against this path
     * @return the resulting path
     *
     * @see #relativize
     */
    @SuppressWarnings("ObjectEquality")
    @Override
    public Path resolve(final Path other)
    {
        checkProvider(other);
        final GenericPath otherPath = (GenericPath) other;

        final PathElements newNames
            = factory.resolve(elements, otherPath.elements);

        /*
         * See PathElementsFactory's .resolve()
         */
        if (newNames == elements)
            return this;
        if (newNames == otherPath.elements)
            return other;

        return new GenericPath(fs, factory, newNames);
    }

    /**
     * Converts a given path string to a {@code Path} and resolves it against
     * this {@code Path} in exactly the manner specified by the {@link
     * #resolve(Path) resolve} method. For example, suppose that the name
     * separator is "{@code /}" and a path represents "{@code foo/bar}", then
     * invoking this method with the path string "{@code gus}" will result in
     * the {@code Path} "{@code foo/bar/gus}".
     *
     * @param other the path string to resolve against this path
     * @return the resulting path
     *
     * @throws InvalidPathException if the path string cannot be converted to
     * a Path.
     * @see FileSystem#getPath
     */
    @Override
    public Path resolve(final String other)
    {
        final PathElements otherElements = factory.toPathElements(other);
        return resolve(new GenericPath(fs, factory, otherElements));
    }

    /**
     * Resolves the given path against this path's {@link #getParent parent}
     * path. This is useful where a file name needs to be <i>replaced</i> with
     * another file name. For example, suppose that the name separator is
     * "{@code /}" and a path represents "{@code dir1/dir2/foo}", then invoking
     * this method with the {@code Path} "{@code bar}" will result in the {@code
     * Path} "{@code dir1/dir2/bar}". If this path does not have a parent path,
     * or {@code other} is {@link #isAbsolute() absolute}, then this method
     * returns {@code other}. If {@code other} is an empty path then this method
     * returns this path's parent, or where this path doesn't have a parent, the
     * empty path.
     *
     * @param other the path to resolve against this path's parent
     * @return the resulting path
     *
     * @see #resolve(Path)
     */
    @Override
    public Path resolveSibling(final Path other)
    {
        checkProvider(other);
        final GenericPath otherPath = (GenericPath) other;

        final PathElements newNames
            = factory.resolveSibling(elements, otherPath.elements);

        /*
         * See PathElementsFactory's .resolve()
         */
        //noinspection ObjectEquality
        if (newNames == otherPath.elements)
            return other;

        return new GenericPath(fs, factory, newNames);
    }

    /**
     * Converts a given path string to a {@code Path} and resolves it against
     * this path's {@link #getParent parent} path in exactly the manner
     * specified by the {@link #resolveSibling(Path) resolveSibling} method.
     *
     * @param other the path string to resolve against this path's parent
     * @return the resulting path
     *
     * @throws InvalidPathException if the path string cannot be converted to
     * a Path.
     * @see FileSystem#getPath
     */
    @Override
    public Path resolveSibling(final String other)
    {
        final PathElements otherElements = factory.toPathElements(other);
        return resolveSibling(new GenericPath(fs, factory, otherElements));
    }

    /**
     * Constructs a relative path between this path and a given path.
     * <p> Relativization is the inverse of {@link #resolve(Path) resolution}.
     * This method attempts to construct a {@link #isAbsolute relative} path
     * that when {@link #resolve(Path) resolved} against this path, yields a
     * path that locates the same file as the given path. For example, on UNIX,
     * if this path is {@code "/a/b"} and the given path is {@code "/a/b/c/d"}
     * then the resulting relative path would be {@code "c/d"}. Where this
     * path and the given path do not have a {@link #getRoot root} component,
     * then a relative path can be constructed. A relative path cannot be
     * constructed if only one of the paths have a root component. Where both
     * paths have a root component then it is implementation dependent if a
     * relative path can be constructed. If this path and the given path are
     * {@link #equals equal} then an <i>empty path</i> is returned.
     * <p> For any two {@link #normalize normalized} paths <i>p</i> and
     * <i>q</i>, where <i>q</i> does not have a root component,
     * <blockquote>
     * <i>p</i><tt>.relativize(</tt><i>p</i><tt>.resolve(</tt><i>q</i><tt>))
     * .equals(</tt><i>q</i><tt>)</tt>
     * </blockquote>
     * <p> When symbolic links are supported, then whether the resulting path,
     * when resolved against this path, yields a path that can be used to locate
     * the {@link Files#isSameFile same} file as {@code other} is implementation
     * dependent. For example, if this path is  {@code "/a/b"} and the given
     * path is {@code "/a/x"} then the resulting relative path may be {@code
     * "../x"}. If {@code "b"} is a symbolic link then is implementation
     * dependent if {@code "a/b/../x"} would locate the same file as {@code
     * "/a/x"}.
     *
     * @param other the path to relativize against this path
     * @return the resulting relative path, or an empty path if both paths are
     * equal
     *
     * @throws IllegalArgumentException if {@code other} is not a {@code
     * Path} that can be relativized
     * against this path
     */
    @Override
    public Path relativize(final Path other)
    {
        checkProvider(other);

        final GenericPath otherPath = (GenericPath) other;
        final PathElements otherElements = otherPath.elements;

        final PathElements relativized
            = factory.relativize(elements, otherElements);

        return new GenericPath(fs, factory, relativized);
    }

    /**
     * Returns a URI to represent this path.
     * <p> This method constructs an absolute {@link URI} with a {@link
     * URI#getScheme() scheme} equal to the URI scheme that identifies the
     * provider. The exact form of the scheme specific part is highly provider
     * dependent.
     * <p> In the case of the default provider, the URI is hierarchical with
     * a {@link URI#getPath() path} component that is absolute. The query and
     * fragment components are undefined. Whether the authority component is
     * defined or not is implementation dependent. There is no guarantee that
     * the {@code URI} may be used to construct a {@link File java.io.File}.
     * In particular, if this path represents a Universal Naming Convention
     * (UNC)
     * path, then the UNC server name may be encoded in the authority component
     * of the resulting URI. In the case of the default provider, and the file
     * exists, and it can be determined that the file is a directory, then the
     * resulting {@code URI} will end with a slash.
     * <p> The default provider provides a similar <em>round-trip</em> guarantee
     * to the {@link File} class. For a given {@code Path} <i>p</i> it
     * is guaranteed that
     * <blockquote><tt>
     * {@link Paths#get(URI) Paths.get}(</tt><i>p</i><tt>.toUri()).equals
     * (</tt><i>p</i>
     * <tt>.{@link #toAbsolutePath() toAbsolutePath}())</tt>
     * </blockquote>
     * so long as the original {@code Path}, the {@code URI}, and the new {@code
     * Path} are all created in (possibly different invocations of) the same
     * Java virtual machine. Whether other providers make any guarantees is
     * provider specific and therefore unspecified.
     * <p> When a file system is constructed to access the contents of a file
     * as a file system then it is highly implementation specific if the
     * returned
     * URI represents the given path in the file system or it represents a
     * <em>compound</em> URI that encodes the URI of the enclosing file system.
     * A format for compound URIs is not defined in this release; such a scheme
     * may be added in a future release.
     *
     * @return the URI representing this path
     *
     * @throws IOError if an I/O error occurs obtaining the absolute path, or
     * where a
     * file system is constructed to access the contents of a file as
     * a file system, and the URI of the enclosing file system cannot be
     * obtained
     * @throws SecurityException In the case of the default provider, and a
     * security manager
     * is installed, the {@link #toAbsolutePath toAbsolutePath} method
     * throws a security exception.
     */
    @Override
    public URI toUri()
    {
        return null;
    }

    /**
     * Returns a {@code Path} object representing the absolute path of this
     * path.
     * <p> If this path is already {@link Path#isAbsolute absolute} then this
     * method simply returns this path. Otherwise, this method resolves the path
     * in an implementation dependent manner, typically by resolving the path
     * against a file system default directory. Depending on the implementation,
     * this method may throw an I/O error if the file system is not accessible.
     *
     * @return a {@code Path} object representing the absolute path
     *
     * @throws IOError if an I/O error occurs
     * @throws SecurityException In the case of the default provider, a
     * security manager
     * is installed, and this path is not absolute, then the security
     * manager's {@link SecurityManager#checkPropertyAccess(String)
     * checkPropertyAccess} method is invoked to check access to the
     * system property {@code user.dir}
     */
    @Override
    public Path toAbsolutePath()
    {
        return null;
    }

    /**
     * Returns the <em>real</em> path of an existing file.
     * <p> The precise definition of this method is implementation dependent but
     * in general it derives from this path, an {@link #isAbsolute absolute}
     * path that locates the {@link Files#isSameFile same} file as this path,
     * but
     * with name elements that represent the actual name of the directories
     * and the file. For example, where filename comparisons on a file system
     * are case insensitive then the name elements represent the names in their
     * actual case. Additionally, the resulting path has redundant name
     * elements removed.
     * <p> If this path is relative then its absolute path is first obtained,
     * as if by invoking the {@link #toAbsolutePath toAbsolutePath} method.
     * <p> The {@code options} array may be used to indicate how symbolic links
     * are handled. By default, symbolic links are resolved to their final
     * target. If the option {@link LinkOption#NOFOLLOW_LINKS NOFOLLOW_LINKS} is
     * present then this method does not resolve symbolic links.
     * Some implementations allow special names such as "{@code ..}" to refer to
     * the parent directory. When deriving the <em>real path</em>, and a
     * "{@code ..}" (or equivalent) is preceded by a non-"{@code ..}" name then
     * an implementation will typically cause both names to be removed. When
     * not resolving symbolic links and the preceding name is a symbolic link
     * then the names are only removed if it guaranteed that the resulting path
     * will locate the same file as this path.
     *
     * @param options options indicating how symbolic links are handled
     * @return an absolute path represent the <em>real</em> path of the file
     * located by this object
     *
     * @throws IOException if the file does not exist or an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager
     * is installed, its {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file, and where
     * this path is not absolute, its {@link
     * SecurityManager#checkPropertyAccess(String)
     * checkPropertyAccess} method is invoked to check access to the
     * system property {@code user.dir}
     */
    @Override
    public Path toRealPath(final LinkOption... options)
        throws IOException
    {
        return null;
    }

    /**
     * Returns a {@link File} object representing this path. Where this {@code
     * Path} is associated with the default provider, then this method is
     * equivalent to returning a {@code File} object constructed with the
     * {@code String} representation of this path.
     * <p> If this path was created by invoking the {@code File} {@link
     * File#toPath toPath} method then there is no guarantee that the {@code
     * File} object returned by this method is {@link #equals equal} to the
     * original {@code File}.
     *
     * @return a {@code File} object representing this path
     *
     * @throws UnsupportedOperationException if this {@code Path} is not
     * associated with the default provider
     */
    @Override
    public File toFile()
    {
        return new File(asString);
    }

    /**
     * Registers the file located by this path with a watch service.
     * <p> In this release, this path locates a directory that exists. The
     * directory is registered with the watch service so that entries in the
     * directory can be watched. The {@code events} parameter is the events to
     * register and may contain the following events:
     * <ul>
     * <li>{@link StandardWatchEventKinds#ENTRY_CREATE ENTRY_CREATE} -
     * entry created or moved into the directory</li>
     * <li>{@link StandardWatchEventKinds#ENTRY_DELETE ENTRY_DELETE} -
     * entry deleted or moved out of the directory</li>
     * <li>{@link StandardWatchEventKinds#ENTRY_MODIFY ENTRY_MODIFY} -
     * entry in directory was modified</li>
     * </ul>
     * <p> The {@link WatchEvent#context context} for these events is the
     * relative path between the directory located by this path, and the path
     * that locates the directory entry that is created, deleted, or modified.
     * <p> The set of events may include additional implementation specific
     * event that are not defined by the enum {@link StandardWatchEventKinds}
     * <p> The {@code modifiers} parameter specifies <em>modifiers</em> that
     * qualify how the directory is registered. This release does not define any
     * <em>standard</em> modifiers. It may contain implementation specific
     * modifiers.
     * <p> Where a file is registered with a watch service by means of a
     * symbolic
     * link then it is implementation specific if the watch continues to depend
     * on the existence of the symbolic link after it is registered.
     *
     * @param watcher the watch service to which this object is to be registered
     * @param events the events for which this object should be registered
     * @param modifiers the modifiers, if any, that modify how the object is
     * registered
     * @return a key representing the registration of this object with the
     * given watch service
     *
     * @throws UnsupportedOperationException if unsupported events or
     * modifiers are specified
     * @throws IllegalArgumentException if an invalid combination of events
     * or modifiers is specified
     * @throws ClosedWatchServiceException if the watch service is closed
     * @throws NotDirectoryException if the file is registered to watch the
     * entries in a directory
     * and the file is not a directory  <i>(optional specific exception)</i>
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file.
     */
    @Override
    public WatchKey register(final WatchService watcher,
        final WatchEvent.Kind<?>[] events,
        final WatchEvent.Modifier... modifiers)
        throws IOException
    {
        // TODO
        return null;
    }

    /**
     * Registers the file located by this path with a watch service.
     * <p> An invocation of this method behaves in exactly the same way as the
     * invocation
     * <pre>
     *     watchable.{@link #register(WatchService, WatchEvent.Kind[],
     *     WatchEvent.Modifier[]) register}(watcher, events, new WatchEvent
     *     .Modifier[0]);
     * </pre>
     * <p> <b>Usage Example:</b>
     * Suppose we wish to register a directory for entry create, delete, and
     * modify
     * events:
     * <pre>
     *     Path dir = ...
     *     WatchService watcher = ...
     *     WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
     *     ENTRY_MODIFY);
     * </pre>
     *
     * @param watcher The watch service to which this object is to be registered
     * @param events The events for which this object should be registered
     * @return A key representing the registration of this object with the
     * given watch service
     *
     * @throws UnsupportedOperationException If unsupported events are specified
     * @throws IllegalArgumentException If an invalid combination of events
     * is specified
     * @throws ClosedWatchServiceException If the watch service is closed
     * @throws NotDirectoryException If the file is registered to watch the
     * entries in a directory
     * and the file is not a directory  <i>(optional specific exception)</i>
     * @throws IOException If an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, the {@link SecurityManager#checkRead(String) checkRead}
     * method is invoked to check read access to the file.
     */
    @Override
    public WatchKey register(final WatchService watcher,
        final WatchEvent.Kind<?>... events)
        throws IOException
    {
        // TODO
        return null;
    }

    /**
     * Returns an iterator over the name elements of this path.
     * <p> The first element returned by the iterator represents the name
     * element that is closest to the root in the directory hierarchy, the
     * second element is the next closest, and so on. The last element returned
     * is the name of the file or directory denoted by this path. The {@link
     * #getRoot root} component, if present, is not returned by the iterator.
     *
     * @return an iterator over the name elements of this path.
     */
    @SuppressWarnings("AnonymousInnerClassWithTooManyMethods")
    @Override
    public Iterator<Path> iterator()
    {
        final Iterator<PathElements> iterator = elements.iterator();

        return new Iterator<Path>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public Path next()
            {
                return new GenericPath(fs, factory, iterator.next());
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Compares two abstract paths lexicographically. The ordering defined by
     * this method is provider specific, and in the case of the default
     * provider, platform specific. This method does not access the file system
     * and neither file is required to exist.
     * <p> This method may not be used to compare paths that are associated
     * with different file system providers.
     *
     * @param other the path compared to this path.
     * @return zero if the argument is {@link #equals equal} to this path, a
     * value less than zero if this path is lexicographically less than
     * the argument, or a value greater than zero if this path is
     * lexicographically greater than the argument
     *
     * @throws ClassCastException if the paths are associated with different providers
     */
    @Override
    public int compareTo(final Path other)
    {
        try {
            checkProvider(other);
        } catch (ProviderMismatchException ignored) {
            // Meh. Required by the contract.
            throw new ClassCastException();
        }
        return asString.compareTo(other.toString());
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     * an execution of a Java application, the {@code hashCode} method
     * must consistently return the same integer, provided no information
     * used in {@code equals} comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     * method, then calling the {@code hashCode} method on each of
     * the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     * according to the {@link Object#equals(Object)}
     * method, then calling the {@code hashCode} method on each of the
     * two objects must produce distinct integer results.  However, the
     * programmer should be aware that producing distinct integer results
     * for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java<font size="-2"><sup>TM</sup></font> programming language.)
     *
     * @return a hash code value for this object.
     *
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(fs, factory, elements);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     *
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final GenericPath other = (GenericPath) obj;
        return fs.equals(other.fs)
            && factory.equals(other.factory)
            && elements.equals(other.elements);
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    @Nonnull
    public String toString()
    {
        return asString;
    }

    private void checkProvider(final Path other)
    {
        if (!fs.provider().equals(other.getFileSystem().provider()))
            throw new ProviderMismatchException();
    }
}
