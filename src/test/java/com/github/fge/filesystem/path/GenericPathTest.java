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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import static com.github.fge.filesystem.path.PathAssert.assertPath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GenericPathTest
{
    private static final String[] NO_NAMES = new String[0];

    private FileSystem fs;
    private PathElementsFactory factory;

    @BeforeMethod
    public void initMocks()
    {
        fs = mock(FileSystem.class);
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
}