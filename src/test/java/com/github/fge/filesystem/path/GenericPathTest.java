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

import com.github.fge.filesystem.CustomSoftAssertions;
import com.github.fge.filesystem.fs.FileSystemBase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static com.github.fge.filesystem.path.PathAssert.assertPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GenericPathTest
{
    private static final String[] NO_NAMES = new String[0];

    private FileSystemBase fs;
    private PathElementsFactory factory;

    @BeforeMethod
    public void initMocks()
    {
        fs = mock(FileSystemBase.class);
        factory = mock(PathElementsFactory.class);
    }

    @Test
    public void isAbsoluteDelegatesToPathElementsFactory()
    {
        final PathElements elements1 = new PathElements("/", NO_NAMES);
        final PathElements elements2 = PathElements.EMPTY;

        when(factory.isAbsolute(elements1)).thenReturn(false);
        when(factory.isAbsolute(elements2)).thenReturn(true);

        Path path;

        path = new GenericPath(fs, factory, elements1);

        assertThat(path.isAbsolute()).isFalse();

        path = new GenericPath(fs, factory, elements2);

        assertThat(path.isAbsolute()).isTrue();
    }

    @Test
    public void getRootWihtoutRootReturnsNull()
    {
        final Path path = new GenericPath(fs, factory, PathElements.EMPTY);

        assertPath(path.getRoot()).isNull();
    }

    @Test
    public void getRootWithRootDoesNotReturnNull()
    {
        final PathElements elements = new PathElements("/", NO_NAMES);
        final Path path = new GenericPath(fs, factory, elements);

        assertPath(path.getRoot()).isNotNull();
    }

    @Test
    public void getFileNameWithNoNamesReturnsNull()
    {
        final Path path = new GenericPath(fs, factory, PathElements.EMPTY);

        assertPath(path.getFileName()).isNull();
    }

    @Test
    public void getFileNameWithNameElementsDoesNotReturnNull()
    {
        final PathElements elements
            = new PathElements(null, new String[] { "foo", "bar" });

        final Path path = new GenericPath(fs, factory, elements);

        assertPath(path.getFileName()).isNotNull();
    }

    /*
     * This test this part of the Path's .relativize() method:
     *
     * <p> For any two {@link #normalize normalized} paths <i>p</i> and
     * <i>q</i>, where <i>q</i> does not have a root component,
     * <blockquote>
     * <i>p</i><tt>.relativize(</tt><i>p</i><tt>.resolve(</tt><i>q</i><tt>))
     * .equals(</tt><i>q</i><tt>)</tt>
     * </blockquote>
     *
     * Unfortunately, that turns out NOT TO BE TRUE! Whether p is absolute or
     * relative, it is indeed the case that the path elements (root, names) are
     * the same but the filesystem DIFFERS.
     *
     * An as Path's .equals() requires that the two filesystems be equal in
     * order for two Paths to be equals, this contract can not be obeyed; or I
     * am doing something VERY wrong.
     */
    @Test(enabled = false)
    public void relativizeResolveRoundRobinWorks()
    {
        /*
         * In order to set up the environment we define a mock
         * FileSystemProvider which both our mock filesystems will return when
         * .provider() is called.
         *
         * We also suppose that the same PathElementsFactory is used; while this
         * code is not written yet, there should be only one such factory per
         * FileSystemProvider anyway (which is fed into all generated FileSystem
         * instances -- at least that's the plan).
         *
         * Note that this test method assumes that .equals() and .hashCode() are
         * not implemented on GenericPath. As such we check that the FileSystem
         * is the same (this is required by Path's equals()) and that the path
         * elements are the same (this is this package's requirements).
         */
        final FileSystemProvider fsProvider = mock(FileSystemProvider.class);
        final PathElementsFactory elementsFactory
            = new UnixPathElementsFactory();
        final FileSystemBase fsForP = mock(FileSystemBase.class);
        final FileSystemBase fsForQ = mock(FileSystemBase.class);

        when(fsForP.provider()).thenReturn(fsProvider);
        when(fsForQ.provider()).thenReturn(fsProvider);

        /*
         * The path to be operated. As the contract says, it has no root
         * component.
         */
        final GenericPath q = new GenericPath(fsForQ, elementsFactory,
            new PathElements(null, new String[] { "q1", "q2" }));

        /*
         * The path against which both resolution and relativization are
         * performed. We take two versions of it: a non absolute one and an
         * absolute one.
         *
         * Note that since we use a UnixPathElementsFactory, we equate an
         * absolute path (or not) to a path which has a root component (or not).
         */
        GenericPath p;
        // "rr" as in "resolved, relativized"
        GenericPath rr;

        final CustomSoftAssertions soft = CustomSoftAssertions.create();

        /*
         * Try with the absolute version first...
         */
        p = new GenericPath(fsForP, elementsFactory,
            new PathElements("/", new String[] { "p1", "p2" }));
        rr = (GenericPath) p.relativize(p.resolve(q));

        soft.assertThat(rr.getFileSystem())
            .as("rr and q filesystems should be the same (p absolute)")
            .isSameAs(q.getFileSystem());
        soft.assertThat(rr.elements).hasSameContentsAs(q.elements);

        /*
         * Now with the non absolute version
         */
        p = new GenericPath(fsForP, elementsFactory,
            new PathElements(null, new String[] { "p1", "p2" }));
        rr = (GenericPath) p.relativize(p.resolve(q));

        soft.assertThat(rr.getFileSystem())
            .as("rr and q filesystems should be the same (p not absolute)")
            .isSameAs(q.getFileSystem());
        soft.assertThat(rr.elements).hasSameContentsAs(q.elements);

        soft.assertAll();
    }
}