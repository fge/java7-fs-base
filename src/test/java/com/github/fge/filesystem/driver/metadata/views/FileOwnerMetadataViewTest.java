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
import com.github.fge.filesystem.driver.metadata.readers
    .FileOwnerAttributeReader;
import com.github.fge.filesystem.driver.metadata.writers
    .FileOwnerAttributeWriter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public final class FileOwnerMetadataViewTest
    extends MetadataViewTest
{
    private FileOwnerAttributeReader<Object> reader;
    private FileOwnerAttributeWriter<Object> writer;
    private FileOwnerMetadataView<Object> view;

    public FileOwnerMetadataViewTest()
    {
        super("owner");
    }

    @SuppressWarnings("unchecked")
    @BeforeMethod
    @Override
    public void init()
    {
        reader = mock(FileOwnerAttributeReader.class);
        writer = mock(FileOwnerAttributeWriter.class);
        driver = mock(MetadataDriver.class);
        when(driver.getAttributeWriter(same(path), same(name)))
            .thenReturn(writer);
        when(driver.getAttributeReader(same(path), same(name)))
            .thenReturn(reader);
        view = new FileOwnerMetadataView<>(path, driver);
    }

    @Test
    public void getOwnerTest()
        throws IOException
    {
        view.getOwner();

        verify(reader, only()).getOwner();
        verifyZeroInteractions(writer);
    }

    @Test(dependsOnMethods = "getOwnerTest")
    public void getOwnerSuccessTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        when(reader.getOwner()).thenReturn(owner);

        final UserPrincipal actual = view.getOwner();

        assertThat(actual).isSameAs(owner);
    }

    @Test(dependsOnMethods = "getOwnerTest")
    public void getOwnerFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(reader.getOwner()).thenThrow(exception);

        try {
            view.getOwner();
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setOwnerTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        view.setOwner(owner);

        verify(writer, only()).setOwner(same(owner));
        verifyZeroInteractions(reader);
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
}
