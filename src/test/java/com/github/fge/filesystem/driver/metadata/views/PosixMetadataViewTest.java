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

package com.github.fge.filesystem.driver.metadata.views;

import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import com.github.fge.filesystem.driver.metadata.writers.PosixAttributeWriter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Random;
import java.util.Set;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public final class PosixMetadataViewTest
    extends MetadataViewTest
{
    private PosixAttributeWriter<Object> writer;
    private PosixMetadataView<Object> view;

    public PosixMetadataViewTest()
    {
        super("posix");
    }

    @SuppressWarnings("unchecked")
    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        writer = mock(PosixAttributeWriter.class);
        driver = mock(MetadataDriver.class);
        when(driver.getAttributeWriter(same(path), same(name)))
            .thenReturn(writer);
        view = new PosixMetadataView<>(path, driver);
    }

    @Test
    public void readAttributesTest()
        throws IOException
    {
        view.readAttributes();

        verify(driver).getAttributesByClass(same(path),
            eq(PosixFileAttributes.class));

        verifyZeroInteractions(writer);
    }

    @Test(dependsOnMethods = "readAttributesTest")
    public void readAttributesSuccessTest()
        throws IOException
    {
        final PosixFileAttributes attributes = mock(PosixFileAttributes.class);

        when(driver.getAttributesByClass(same(path),
            eq(PosixFileAttributes.class))).thenReturn(attributes);

        final PosixFileAttributes actual = view.readAttributes();

        assertThat(actual).isSameAs(attributes);
    }

    @Test(dependsOnMethods = "readAttributesTest")
    public void readAttributesFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(driver.getAttributesByClass(same(path),
            eq(PosixFileAttributes.class))).thenThrow(exception);

        try {
            view.readAttributes();
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setTimesTest()
        throws IOException
    {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        final Random random = new Random();

        final FileTime lastModifiedTime
            = FileTime.fromMillis(random.nextLong());
        final FileTime lastAccessTime = FileTime.fromMillis(random.nextLong());
        final FileTime creationTime = FileTime.fromMillis(random.nextLong());

        view.setTimes(lastModifiedTime, lastAccessTime, creationTime);

        verify(writer, only()).setTimes(same(lastModifiedTime), same(
            lastAccessTime), same(creationTime));
    }

    @Test(dependsOnMethods = "setTimesTest")
    public void setTimesFailureTest()
        throws IOException
    {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        final Random random = new Random();

        final FileTime lastModifiedTime
            = FileTime.fromMillis(random.nextLong());
        final FileTime lastAccessTime = FileTime.fromMillis(random.nextLong());
        final FileTime creationTime = FileTime.fromMillis(random.nextLong());

        final IOException exception = new IOException();

        doThrow(exception).when(writer).setTimes(any(FileTime.class),
            any(FileTime.class), any(FileTime.class));

        try {
            view.setTimes(lastModifiedTime, lastAccessTime, creationTime);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test(dependsOnMethods = "readAttributesSuccessTest")
    public void getOwnerTest()
        throws IOException
    {
        final PosixFileAttributes attributes = mock(PosixFileAttributes.class);

        when(driver.getAttributesByClass(same(path), eq(
            PosixFileAttributes.class))).thenReturn(attributes);

        view.getOwner();

        verify(driver).getAttributesByClass(same(path),
            eq(PosixFileAttributes.class));
        verify(attributes, only()).owner();
    }

    @Test(dependsOnMethods = "getOwnerTest")
    public void getOwnerSuccessTest()
        throws IOException
    {
        final PosixFileAttributes attributes = mock(PosixFileAttributes.class);
        final UserPrincipal owner = mock(UserPrincipal.class);

        when(attributes.owner()).thenReturn(owner);

        when(driver.getAttributesByClass(same(path), eq(
            PosixFileAttributes.class))).thenReturn(attributes);

        final UserPrincipal actual = view.getOwner();
        assertThat(actual).isSameAs(owner);
    }

    @Test
    public void setOwnerTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        view.setOwner(owner);

        verify(writer, only()).setOwner(same(owner));
    }

    @Test(dependsOnMethods = "setOwnerTest")
    public void setOwnerFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setOwner(any(UserPrincipal.class));

        try {
            view.setOwner(mock(UserPrincipal.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setGroupTest()
        throws IOException
    {
        final GroupPrincipal group = mock(GroupPrincipal.class);

        view.setGroup(group);

        verify(writer, only()).setGroup(same(group));
    }

    @Test(dependsOnMethods = "setGroupTest")
    public void setGroupFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setGroup(any(GroupPrincipal.class));

        try {
            view.setGroup(mock(GroupPrincipal.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setPermissionsTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final Set<PosixFilePermission> permissions = mock(Set.class);

        view.setPermissions(permissions);

        verify(writer, only()).setPermissions(same(permissions));
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "setPermissionsTest")
    public void setPermissionsFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setPermissions(anySet());

        try {
            view.setPermissions(mock(Set.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }
}
