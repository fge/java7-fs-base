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

package com.github.fge.filesystem.driver.metadata.read;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class BasicViewReaderTest
    extends ViewReaderTest<BasicViewReader, BasicFileAttributeView>
{
    @SuppressWarnings("UnsecureRandomNumberGeneration")
    private final Random random = new Random();
    private BasicFileAttributes attrs;

    public BasicViewReaderTest()
    {
        super(BasicFileAttributeView.class, "lastModifiedTime",
            "lastAccessTime", "creationTime", "size", "isRegularFile",
            "isDirectory", "isSymbolicLink", "isOther", "fileKey");
    }

    @Override
    protected void initReader()
        throws IOException
    {
        attrs = mock(BasicFileAttributes.class);
        when(view.readAttributes()).thenReturn(attrs);
        reader = new BasicViewReader(view);
    }

    @Test
    public void readLastModifiedTimeTest()
        throws IOException
    {
        reader.getAttributeByName("lastModifiedTime");

        verify(view, only()).readAttributes();
        verify(attrs, only()).lastModifiedTime();
    }

    @Test(dependsOnMethods = "readLastModifiedTimeTest")
    public void readLastModifiedTimeSuccessTest()
        throws IOException
    {
        final FileTime lastModifiedTime = someFileTime();

        when(attrs.lastModifiedTime()).thenReturn(lastModifiedTime);

        final Object actual = reader.getAttributeByName("lastModifiedTime");

        assertThat(actual).isSameAs(lastModifiedTime);
    }

    @Test
    public void readLastAccessTimeTest()
        throws IOException
    {
        reader.getAttributeByName("lastAccessTime");

        verify(view, only()).readAttributes();
        verify(attrs, only()).lastAccessTime();
    }

    @Test(dependsOnMethods = "readLastAccessTimeTest")
    public void readLastAccessTimeSuccessTest()
        throws IOException
    {
        final FileTime lastAccessTime = someFileTime();

        when(attrs.lastAccessTime()).thenReturn(lastAccessTime);

        final Object actual = reader.getAttributeByName("lastAccessTime");

        assertThat(actual).isSameAs(lastAccessTime);
    }

    @Test
    public void readCreationTimeTest()
        throws IOException
    {
        reader.getAttributeByName("creationTime");

        verify(view, only()).readAttributes();
        verify(attrs, only()).creationTime();
    }

    @Test(dependsOnMethods = "readCreationTimeTest")
    public void readCreationTimeSuccessTest()
        throws IOException
    {
        final FileTime creationTime = someFileTime();

        when(attrs.creationTime()).thenReturn(creationTime);

        final Object actual = reader.getAttributeByName("creationTime");

        assertThat(actual).isSameAs(creationTime);
    }

    @Test
    public void readSizeTest()
        throws IOException
    {
        reader.getAttributeByName("size");

        verify(view, only()).readAttributes();
        verify(attrs, only()).size();
    }

    @Test(dependsOnMethods = "readSizeTest")
    public void readSizeSuccessTest()
        throws IOException
    {
        final long size = 42L;

        when(attrs.size()).thenReturn(size);

        final Object actual = reader.getAttributeByName("size");

        assertThat(actual).isEqualTo(size);
    }

    @Test
    public void readIsRegularFileTest()
        throws IOException
    {
        reader.getAttributeByName("isRegularFile");

        verify(view, only()).readAttributes();
        verify(attrs, only()).isRegularFile();
    }

    @Test(dependsOnMethods = "readIsRegularFileTest")
    public void readIsRegularFileSuccessTest()
        throws IOException
    {
        final boolean isRegularFile = true;

        when(attrs.isRegularFile()).thenReturn(isRegularFile);

        final Object actual = reader.getAttributeByName("isRegularFile");

        assertThat(actual).isEqualTo(isRegularFile);
    }

    @Test
    public void readIsDirectoryTest()
        throws IOException
    {
        reader.getAttributeByName("isDirectory");

        verify(view, only()).readAttributes();
        verify(attrs, only()).isDirectory();
    }

    @Test(dependsOnMethods = "readIsDirectoryTest")
    public void readIsDirectorySuccessTest()
        throws IOException
    {
        final boolean isDirectory = true;

        when(attrs.isDirectory()).thenReturn(isDirectory);

        final Object actual = reader.getAttributeByName("isDirectory");

        assertThat(actual).isEqualTo(isDirectory);
    }

    @Test
    public void readIsSymbolicLinkTest()
        throws IOException
    {
        reader.getAttributeByName("isSymbolicLink");

        verify(view, only()).readAttributes();
        verify(attrs, only()).isSymbolicLink();
    }

    @Test(dependsOnMethods = "readIsSymbolicLinkTest")
    public void readIsSymbolicLinkSuccessTest()
        throws IOException
    {
        final boolean isSymbolicLink = true;

        when(attrs.isSymbolicLink()).thenReturn(isSymbolicLink);

        final Object actual = reader.getAttributeByName("isSymbolicLink");

        assertThat(actual).isEqualTo(isSymbolicLink);
    }

    @Test
    public void readIsOtherTest()
        throws IOException
    {
        reader.getAttributeByName("isOther");

        verify(view, only()).readAttributes();
        verify(attrs, only()).isOther();
    }

    @Test(dependsOnMethods = "readIsOtherTest")
    public void readIsOtherTestSuccess()
        throws IOException
    {
        final boolean isOther = true;

        when(attrs.isOther()).thenReturn(isOther);

        final Object actual = reader.getAttributeByName("isOther");

        assertThat(actual).isEqualTo(isOther);
    }

    @Test
    public void readFileKeyTest()
        throws IOException
    {
        reader.getAttributeByName("fileKey");

        verify(view, only()).readAttributes();
        verify(attrs, only()).fileKey();
    }

    @Test(dependsOnMethods = "readFileKeyTest")
    public void readFileKeySuccessTest()
        throws IOException
    {
        final Object fileKey = new Object();

        when(attrs.fileKey()).thenReturn(fileKey);

        final Object actual = reader.getAttributeByName("fileKey");

        assertThat(actual).isSameAs(fileKey);
    }

    @DataProvider
    public Iterator<Object[]> allAttributeNames()
    {
        final List<Object[]> list = new ArrayList<>();

        for (final String name: definedAttributes)
            list.add(new Object[] { name });

        return list.iterator();
    }

    @Test(dataProvider = "allAttributeNames")
    public void readAttrFailureTest(final String attrName)
        throws IOException
    {
        final IOException exception = new IOException();

        when(view.readAttributes()).thenThrow(exception);

        try {
            reader.getAttributeByName(attrName);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    private FileTime someFileTime()
    {
        return FileTime.fromMillis(random.nextLong());
    }
}
