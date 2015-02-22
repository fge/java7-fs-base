/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.driver.metadata.write;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public final class PosixViewWriterTest
    extends ViewWriterTest<PosixViewWriter, PosixFileAttributeView>
{
    @SuppressWarnings("UnsecureRandomNumberGeneration")
    private final Random random = new Random();

    public PosixViewWriterTest()
    {
        super(PosixFileAttributeView.class);
    }

    @Override
    protected void initWriter()
        throws IOException
    {
        writer = new PosixViewWriter(view);
    }

    @Test
    public void setLastModifiedTimeSuccessTest()
        throws IOException
    {
        final FileTime lastModifiedTime = someFileTime();

        writer.setAttributeByName("lastModifiedTime", lastModifiedTime);

        verify(view, only()).setTimes(same(lastModifiedTime), nullFileTime(),
            nullFileTime());
    }

    @Test(dependsOnMethods = "setLastModifiedTimeSuccessTest")
    public void setLastModifiedTimeFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setTimes(anyFileTime(), anyFileTime(),
            anyFileTime());

        try {
            writer.setAttributeByName("lastModifiedTime", someFileTime());
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setLastAccessTimeSuccessTest()
        throws IOException
    {
        final FileTime lastAccessTime = someFileTime();

        writer.setAttributeByName("lastAccessTime", lastAccessTime);

        verify(view, only()).setTimes(nullFileTime(), same(lastAccessTime),
            nullFileTime());
    }

    @Test(dependsOnMethods = "setLastAccessTimeSuccessTest")
    public void setLastAccessTimeFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setTimes(anyFileTime(), anyFileTime(),
            anyFileTime());

        try {
            writer.setAttributeByName("lastAccessTime", someFileTime());
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setCreationTimeSuccessTest()
        throws IOException
    {
        final FileTime creationTime = someFileTime();

        writer.setAttributeByName("creationTime", creationTime);

        verify(view, only()).setTimes(nullFileTime(), nullFileTime(),
            same(creationTime));
    }

    @Test(dependsOnMethods = "setCreationTimeSuccessTest")
    public void setCreationTimeFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setTimes(anyFileTime(), anyFileTime(),
            anyFileTime());

        try {
            writer.setAttributeByName("creationTime", someFileTime());
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setOwnerSuccessTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        writer.setAttributeByName("owner", owner);

        verify(view, only()).setOwner(same(owner));
    }

    @Test(dependsOnMethods = "setOwnerSuccessTest")
    public void setOwnerFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setOwner(any(UserPrincipal.class));

        try {
            writer.setAttributeByName("owner", mock(UserPrincipal.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setGroupSuccessTest()
        throws IOException
    {
        final GroupPrincipal group = mock(GroupPrincipal.class);

        writer.setAttributeByName("group", group);

        verify(view, only()).setGroup(same(group));
    }

    @Test(dependsOnMethods = "setGroupSuccessTest")
    public void setGroupFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setGroup(any(GroupPrincipal.class));

        try {
            writer.setAttributeByName("group", mock(GroupPrincipal.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setPermissionsSuccessTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final Set<PosixFilePermission> permissions = mock(Set.class);

        writer.setAttributeByName("permissions", permissions);

        verify(view, only()).setPermissions(same(permissions));
    }

    @Test(dependsOnMethods = "setPermissionsSuccessTest")
    public void setPermissionsFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        //noinspection unchecked
        doThrow(exception).when(view).setPermissions(anySet());

        try {
            writer.setAttributeByName("permissions", mock(Set.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @DataProvider
    public Iterator<Object[]> readOnlyAttrs()
    {
        final List<Object[]> list = new ArrayList<>();

        for (final String name: Arrays.asList("size", "isRegularFile",
            "isDirectory", "isSymbolicLink", "isOther", "fileKey"))
            list.add(new Object[] { name });

        return list.iterator();
    }

    @Test(dataProvider = "readOnlyAttrs")
    public void readOnlyAttributesTest(final String name)
        throws IOException
    {
        try {
            writer.setAttributeByName(name, new Object());
            shouldHaveThrown(ReadOnlyAttributeException.class);
        } catch (ReadOnlyAttributeException e) {
            assertThat(e).hasMessage(name);
        }
    }

    private FileTime someFileTime()
    {
        return FileTime.fromMillis(random.nextLong());
    }

    private static FileTime anyFileTime()
    {
        return any(FileTime.class);
    }

    private static FileTime nullFileTime()
    {
        return isNull(FileTime.class);
    }
}
