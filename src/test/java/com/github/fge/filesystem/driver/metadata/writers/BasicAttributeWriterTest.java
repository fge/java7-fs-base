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

package com.github.fge.filesystem.driver.metadata.writers;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class BasicAttributeWriterTest
    extends AttributeWriterTest<BasicAttributeWriter<Object>>
{
    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        writer = spy(new BasicAttributeWriter<Object>(path, driver)
        {
        });

        doNothing().when(writer).setTimes(any(FileTime.class),
            any(FileTime.class), any(FileTime.class));
    }

    @Test
    public void setLastModifiedTimeTest()
        throws IOException
    {
        writer.setAttributeByName("lastModifiedTime", fileTime);

        verify(writer).setTimes(same(fileTime), nullFileTime(), nullFileTime());
    }

    @Test
    public void setLastAccessTimeTest()
        throws IOException
    {
        writer.setAttributeByName("lastAccessTime", fileTime);

        verify(writer).setTimes(nullFileTime(), same(fileTime), nullFileTime());
    }

    @Test
    public void setCreationTimeTest()
        throws IOException
    {
        writer.setAttributeByName("creationTime", fileTime);

        verify(writer).setTimes(nullFileTime(), nullFileTime(), same(fileTime));
    }

    @DataProvider
    public Iterator<Object[]> readOnlyAttributes()
    {
        final List<Object[]> list = new ArrayList<>();

        for (final String attr: Arrays.asList("size", "isRegularFile",
            "isDirectory", "isSymbolicLink", "isOther", "fileKey"))
            list.add(new Object[] { attr });

        return list.iterator();
    }

    @Test(dataProvider = "readOnlyAttributes")
    public void readOnlyAttributesTest(final String name)
        throws IOException
    {
        final Object object = new Object();

        try {
            writer.setAttributeByName(name, object);
            shouldHaveThrown(ReadOnlyAttributeException.class);
        } catch (ReadOnlyAttributeException e) {
            assertThat(e).hasMessage(name);
        }

        verify(writer, only()).setAttributeByName(same(name), same(object));
    }
}
