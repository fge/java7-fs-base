package com.github.fge.jsr203.attrs.api.byname;

import org.mockito.ArgumentCaptor;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public final class UserDefinedAttributeDispatcherTest
{
    private static final String ATTRNAME1 = "attr1";
    private static final int ATTRSIZE1 = 12;
    private static final String ATTRNAME2 = "attr2";
    private static final int ATTRSIZE2 = 20;


    private UserDefinedFileAttributeView view;
    private NamedAttributeDispatcher dispatcher;

    @BeforeMethod
    public void initDispatcher()
    {
        view = mock(UserDefinedFileAttributeView.class);
        dispatcher = new UserDefinedAttributeDispatcher<>(view);
    }

    @Test
    public void readByNameNoSuchAttributeTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTRNAME1)).thenReturn(false);
        when(view.list()).thenReturn(names);

        try {
            dispatcher.readByName(ATTRNAME1);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).hasMessage(String.format(
                UserDefinedAttributeDispatcher.NO_SUCH_ATTRIBUTE, ATTRNAME1
            ));
        }
    }

    @Test
    public void readByNameFailureTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTRNAME1)).thenReturn(true);
        when(view.list()).thenReturn(names);

        when(view.size(ATTRNAME1)).thenReturn(ATTRSIZE1);

        final IOException exception = new IOException();
        when(view.read(eq(ATTRNAME1), any(ByteBuffer.class)))
            .thenThrow(exception);

        try {
            dispatcher.readByName(ATTRNAME1);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @SuppressWarnings("UnsecureRandomNumberGeneration")
    @Test
    public void readByNameSuccessTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTRNAME1)).thenReturn(true);
        when(view.list()).thenReturn(names);

        when(view.size(ATTRNAME1)).thenReturn(ATTRSIZE1);

        final byte[] array = new byte[ATTRSIZE1];

        new Random(System.currentTimeMillis()).nextBytes(array);

        doAnswer(invocation -> {
            final ByteBuffer buf
                = (ByteBuffer) invocation.getArguments()[1];
            buf.put(array);
            return null;
        }).when(view).read(eq(ATTRNAME1), any(ByteBuffer.class));

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        final Object actual = dispatcher.readByName(ATTRNAME1);

        verify(view).read(eq(ATTRNAME1), captor.capture());

        final ByteBuffer expected = captor.getValue();

        assertThat(actual).isSameAs(expected);

        assertThat(expected.limit()).isEqualTo(ATTRSIZE1);
        assertThat(expected.array()).containsExactly(array);
    }

    @Test
    public void writeByNameFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();
        when(view.write(anyString(), any())).thenThrow(exception);

        try {
            dispatcher.writeByBame(ATTRNAME1, new byte[0]);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void writeByNameByteBufferSuccessTest()
        throws IOException
    {
        final byte[] array = new byte[ATTRSIZE1];

        //noinspection UnsecureRandomNumberGeneration
        new Random(System.currentTimeMillis()).nextBytes(array);

        final ByteBuffer buf = ByteBuffer.wrap(array);

        dispatcher.writeByBame(ATTRNAME1, buf);

        verify(view, only()).write(eq(ATTRNAME1), same(buf));
    }

    @Test
    public void writeByNameByteArraySuccessTest()
        throws IOException
    {
        final byte[] array = new byte[ATTRSIZE1];

        //noinspection UnsecureRandomNumberGeneration
        new Random(System.currentTimeMillis()).nextBytes(array);

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        dispatcher.writeByBame(ATTRNAME1, array);

        verify(view).write(eq(ATTRNAME1), captor.capture());

        final ByteBuffer buf = captor.getValue();

        assertThat(buf.array()).containsExactly(array);
    }
}
