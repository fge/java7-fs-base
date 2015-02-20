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
import com.github.fge.filesystem.driver.metadata.writers.DosAttributeWriter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Random;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public final class DosMetadataViewTest
    extends MetadataViewTest
{
    private DosAttributeWriter<Object> writer;
    private DosMetadataView<Object> view;

    public DosMetadataViewTest()
    {
        super("dos");
    }

    @SuppressWarnings("unchecked")
    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        writer = mock(DosAttributeWriter.class);
        driver = mock(MetadataDriver.class);
        when(driver.getAttributeWriter(same(path), same(name)))
            .thenReturn(writer);
        view = new DosMetadataView<>(path, driver);
    }

    @Test
    public void readAttributesTest()
        throws IOException
    {
        view.readAttributes();

        verify(driver).getAttributesByClass(same(path),
            eq(DosFileAttributes.class));

        verifyZeroInteractions(writer);
    }

    @Test(dependsOnMethods = "readAttributesTest")
    public void readAttributesSuccessTest()
        throws IOException
    {
        final DosFileAttributes attributes = mock(DosFileAttributes.class);

        when(driver.getAttributesByClass(same(path),
            eq(DosFileAttributes.class))).thenReturn(attributes);

        final DosFileAttributes actual = view.readAttributes();

        assertThat(actual).isSameAs(attributes);
    }

    @Test(dependsOnMethods = "readAttributesTest")
    public void readAttributesFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(driver.getAttributesByClass(same(path),
            eq(DosFileAttributes.class))).thenThrow(exception);

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

    @Test
    public void setArchiveTest()
        throws IOException
    {
        view.setArchive(true);

        verify(writer, only()).setArchive(true);
    }

    @Test(dependsOnMethods = "setArchiveTest")
    public void setArchiveFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setArchive(anyBoolean());

        try {
            view.setArchive(true);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setSystemTest()
        throws IOException
    {
        view.setSystem(true);

        verify(writer, only()).setSystem(true);
    }

    @Test(dependsOnMethods = "setSystemTest")
    public void setSystemFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setSystem(anyBoolean());

        try {
            view.setSystem(true);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setHiddenTest()
        throws IOException
    {
        view.setHidden(true);

        verify(writer, only()).setHidden(true);
    }

    @Test(dependsOnMethods = "setHiddenTest")
    public void setHiddenFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setHidden(anyBoolean());

        try {
            view.setHidden(true);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setReadOnlyTest()
        throws IOException
    {
        view.setReadOnly(true);

        verify(writer, only()).setReadOnly(true);
    }

    @Test(dependsOnMethods = "setReadOnlyTest")
    public void setReadOnlyFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(writer).setReadOnly(anyBoolean());

        try {
            view.setReadOnly(true);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }
}
