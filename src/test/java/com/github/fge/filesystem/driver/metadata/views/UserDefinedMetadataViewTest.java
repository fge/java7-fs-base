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

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;
import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UserDefinedMetadataViewTest
    extends MetadataViewTest
{
    private UserDefinedMetadataView<Object> view;

    public UserDefinedMetadataViewTest()
    {
        super("user");
    }

    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        view = spy(new UserDefinedMetadataView<Object>(path, driver)
        {
            @Override
            public List<String> list()
                throws IOException
            {
                return null;
            }

            @Override
            public int size(final String name)
                throws IOException
            {
                return 0;
            }

            @Override
            public int read(final String name, final ByteBuffer dst)
                throws IOException
            {
                return 0;
            }

            @Override
            public int write(final String name, final ByteBuffer src)
                throws IOException
            {
                return 0;
            }

            @Override
            public void delete(final String name)
                throws IOException
            {
            }
        });
    }

    @Test
    public void getAttributeByNameListFailTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(view.list()).thenThrow(exception);

        try {
            view.getAttributeByName("foo");
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void getAttributeByNameNoAttrTest()
        throws IOException
    {
        final String attrName = "foo";
        @SuppressWarnings("unchecked")
        final List<String> attrList = mock(List.class);

        // This is the default but let's make it explicit
        when(attrList.contains(anyString())).thenReturn(false);

        when(view.list()).thenReturn(attrList);

        try {
            view.getAttributeByName(attrName);
            shouldHaveThrown(NoSuchAttributeException.class);
        } catch (NoSuchAttributeException e) {
            assertThat(e.getMessage()).isSameAs(attrName);
        }
    }

    @Test
    public void getAttributeByNameReadFailTest()
        throws IOException
    {
        final String attrName = "foo";
        @SuppressWarnings("unchecked")
        final List<String> attrList = mock(List.class);

        when(attrList.contains(same(attrName))).thenReturn(true);

        when(view.list()).thenReturn(attrList);

        final IOException exception = new IOException();

        when(view.read(same(attrName), any(ByteBuffer.class)))
            .thenThrow(exception);

        try {
            view.getAttributeByName(attrName);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void getAttributeByNameTest()
        throws IOException
    {
        final String attrName = "foo";
        @SuppressWarnings("unchecked")
        final List<String> attrList = mock(List.class);
        final int size = 42;

        when(attrList.contains(anyString())).thenReturn(true);
        when(view.list()).thenReturn(attrList);
        when(view.size(same(attrName))).thenReturn(size);
        when(view.read(same(attrName), any(ByteBuffer.class)))
            .thenReturn(size);

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        final Object actual = view.getAttributeByName(attrName);

        verify(view).list();
        verify(view).size(same(attrName));
        verify(view).read(same(attrName), captor.capture());

        final ByteBuffer buf = captor.getValue();

        assertThat(buf.capacity()).isEqualTo(size);
        assertThat(actual).isSameAs(buf.array());
    }

    @Test
    public void setAttributeByNameFailureTest()
        throws IOException
    {
        final String attrName = "foo";

        final IOException exception = new IOException();

        doThrow(exception).when(view)
            .write(same(attrName), any(ByteBuffer.class));

        try {
            view.setAttributeByName(attrName, mock(ByteBuffer.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void setAttributeByNameTest()
        throws IOException
    {
        final String attrName = "foo";
        final ByteBuffer buf = ByteBuffer.allocate(42);

        view.setAttributeByName(attrName, buf);

        verify(view).write(same(attrName), same(buf));
    }

    @Test
    public void setAttributeByNameByteArrayTest()
        throws IOException
    {
        final String attrName = "foo";
        final byte[] array = new byte[42];

        view.setAttributeByName(attrName, array);

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        verify(view).write(same(attrName), captor.capture());

        assertThat(captor.getValue().array()).isSameAs(array);
    }
}
