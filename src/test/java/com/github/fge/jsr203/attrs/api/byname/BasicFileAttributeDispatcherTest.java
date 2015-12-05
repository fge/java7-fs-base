package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public final class BasicFileAttributeDispatcherTest
{
    private BasicFileAttributes attributes;
    private BasicFileAttributeView view;
    private NamedAttributeDispatcher dispatcher;

    @BeforeMethod
    public void initDispatcher()
        throws IOException
    {
        attributes = mock(BasicFileAttributes.class);
        view = mock(BasicFileAttributeView.class);
        when(view.readAttributes()).thenReturn(attributes);
        dispatcher = new BasicFileAttributeDispatcher<>(view);
    }

    @Test
    public void readLastModifiedTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.fromMillis(System.currentTimeMillis());
        when(attributes.lastModifiedTime()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.LAST_MODIFIED_TIME);

        assertThat(actual).isSameAs(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).lastModifiedTime();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void writeLastModifiedTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.fromMillis(System.currentTimeMillis());

        dispatcher.writeByBame(StandardAttributeNames.LAST_MODIFIED_TIME,
            value);

        verify(view, only()).setTimes(same(value), eq(null), eq(null));
    }

    @Test
    public void readLastAccessTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.fromMillis(System.currentTimeMillis());
        when(attributes.lastAccessTime()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.LAST_ACCESS_TIME);

        assertThat(actual).isSameAs(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).lastAccessTime();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void writeLastAccessTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.fromMillis(System.currentTimeMillis());

        dispatcher.writeByBame(StandardAttributeNames.LAST_ACCESS_TIME,
            value);

        verify(view, only()).setTimes(eq(null), same(value), eq(null));
    }

    @Test
    public void readCreationTimeTest()
        throws IOException
    {
        final FileTime expected
            = FileTime.fromMillis(System.currentTimeMillis());
        when(attributes.creationTime()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.CREATION_TIME);

        assertThat(actual).isSameAs(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).creationTime();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void writeCreationTimeTest()
        throws IOException
    {
        final FileTime value
            = FileTime.fromMillis(System.currentTimeMillis());

        dispatcher.writeByBame(StandardAttributeNames.CREATION_TIME,
            value);

        verify(view, only()).setTimes(eq(null), eq(null), same(value));
    }

    @Test
    public void readSizeTest()
        throws IOException
    {
        @SuppressWarnings("UnsecureRandomNumberGeneration")
        final Long expected = new Random(System.currentTimeMillis()).nextLong();

        when(attributes.size()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.SIZE);

        assertThat(actual).isEqualTo(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).size();

        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void readIsRegularFileTest()
        throws IOException
    {
        final Boolean expected = true;
        when(attributes.isRegularFile()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.IS_REGULAR_FILE);

        assertThat(actual).isEqualTo(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).isRegularFile();

        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void readIsDirectoryTest()
        throws IOException
    {
        final Boolean expected = true;
        when(attributes.isDirectory()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.IS_DIRECTORY);

        assertThat(actual).isEqualTo(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).isDirectory();

        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void readIsSymbolicLinkTest()
        throws IOException
    {
        final Boolean expected = true;
        when(attributes.isSymbolicLink()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.IS_SYMBOLIC_LINK);

        assertThat(actual).isEqualTo(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).isSymbolicLink();

        inOrder.verifyNoMoreInteractions();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void readIsOtherTest()
        throws IOException
    {
        final Boolean expected = true;
        when(attributes.isOther()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.IS_OTHER);

        assertThat(actual).isEqualTo(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).isOther();

        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void readFileKeyTest()
        throws IOException
    {
        final Object expected = new Object();
        when(attributes.fileKey()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.FILE_KEY);

        assertThat(actual).isSameAs(expected);

        final InOrder inOrder = inOrder(view, attributes);

        inOrder.verify(view).readAttributes();
        inOrder.verify(attributes).fileKey();

        inOrder.verifyNoMoreInteractions();
    }
}
