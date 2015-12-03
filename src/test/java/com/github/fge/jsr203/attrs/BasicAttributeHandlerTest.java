package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.StandardAttributeNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasicAttributeHandlerTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private BasicFileAttributes attributes;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private BasicFileAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileAttributeHandlerWithAttributes<?, ?> handler;

    @BeforeMethod
    public void initHandler()
        throws IOException
    {
        attributes = mock(BasicFileAttributes.class);
        view = mock(BasicFileAttributeView.class);
        when(view.readAttributes()).thenReturn(attributes);
        handler = new BasicAttributeHandler(view);
    }

    @Test
    public void readAttributesTest()
        throws IOException
    {
        assertThat(handler.getAttributes()).isSameAs(attributes);
    }

    @Test
    public void readLastModifiedTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        when(attributes.lastModifiedTime()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.LAST_MODIFIED_TIME);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).lastModifiedTime();
    }

    @Test
    public void readLastAccessTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        when(attributes.lastAccessTime()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.LAST_ACCESS_TIME);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).lastAccessTime();
    }

    @Test
    public void readCreationTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        when(attributes.creationTime()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.CREATION_TIME);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).creationTime();
    }

    @Test
    public void readSizeTest()
        throws IOException
    {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        final long expected = new Random(System.currentTimeMillis()).nextLong();
        when(attributes.size()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.SIZE);

        assertThat(actual).isEqualTo(expected);

        verify(attributes, only()).size();
    }

    @Test
    public void readIsRegularFileTest()
        throws IOException
    {
        when(attributes.isRegularFile()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.IS_REGULAR_FILE);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isRegularFile();
    }

    @Test
    public void readIsDirectoryTest()
        throws IOException
    {
        when(attributes.isDirectory()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.IS_DIRECTORY);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isDirectory();
    }

    @Test
    public void readIsSymbolicLinkTest()
        throws IOException
    {
        when(attributes.isSymbolicLink()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.IS_SYMBOLIC_LINK);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isSymbolicLink();
    }

    @Test
    public void readIsOtherTest()
        throws IOException
    {
        when(attributes.isOther()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.IS_OTHER);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isOther();
    }

    @Test
    public void readFileKeyTest()
        throws IOException
    {
        final Object expected = new Object();
        when(attributes.fileKey()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.FILE_KEY);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).fileKey();
    }

    @Test
    public void writeLastModifiedTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        handler.writeAttribute(StandardAttributeNames.LAST_MODIFIED_TIME,
            value);

        verify(view, only()).setTimes(same(value), eq(null), eq(null));
    }

    @Test
    public void writeLastAccessTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        handler.writeAttribute(StandardAttributeNames.LAST_ACCESS_TIME, value);

        verify(view, only()).setTimes(eq(null), same(value), eq(null));
    }

    @Test
    public void writeCreationTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        handler.writeAttribute(StandardAttributeNames.CREATION_TIME, value);

        verify(view, only()).setTimes(eq(null), eq(null), same(value));

    }
}
