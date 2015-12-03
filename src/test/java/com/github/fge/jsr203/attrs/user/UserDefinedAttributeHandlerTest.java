package com.github.fge.jsr203.attrs.user;

import com.github.fge.jsr203.attrs.AttributeHandler;
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

public class UserDefinedAttributeHandlerTest
{
    private static final String ATTR_NAME = "myattr";
    private static final int ATTR_SIZE = 12;

    private UserDefinedFileAttributeView view;
    private AttributeHandler<?> handler;

    @BeforeMethod
    public void initHandler()
    {
        view = mock(UserDefinedFileAttributeView.class);
        handler = new UserDefinedAttributeHandler(view);
    }

    @Test
    public void readAttributeNoSuchAttrTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTR_NAME)).thenReturn(false);
        when(view.list()).thenReturn(names);

        try {
            handler.readAttribute(ATTR_NAME);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).hasMessage(String.format(
                UserDefinedAttributeHandler.NO_SUCH_ATTRIBUTE, ATTR_NAME));
        }
    }

    @Test
    public void readAttributeFailureTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTR_NAME)).thenReturn(true);
        when(view.list()).thenReturn(names);

        when(view.size(ATTR_NAME)).thenReturn(ATTR_SIZE);

        final IOException exception = new IOException();
        when(view.read(eq(ATTR_NAME), any(ByteBuffer.class)))
            .thenThrow(exception);

        try {
            handler.readAttribute(ATTR_NAME);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void readAttributeSuccessTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<String> names = mock(List.class);
        when(names.contains(ATTR_NAME)).thenReturn(true);
        when(view.list()).thenReturn(names);

        when(view.size(ATTR_NAME)).thenReturn(ATTR_SIZE);

        final byte[] array = new byte[ATTR_SIZE];

        //noinspection UnsecureRandomNumberGeneration
        new Random(System.currentTimeMillis()).nextBytes(array);

        doAnswer(invocation -> {
            final ByteBuffer buf
                = (ByteBuffer) invocation.getArguments()[1];
            buf.put(array);
            return null;
        }).when(view).read(eq(ATTR_NAME), any(ByteBuffer.class));

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        final Object actual = handler.readAttribute(ATTR_NAME);

        verify(view).read(eq(ATTR_NAME), captor.capture());

        final ByteBuffer expected = captor.getValue();

        assertThat(actual).isSameAs(expected);

        assertThat(expected.limit()).isEqualTo(ATTR_SIZE);
        assertThat(expected.array()).containsExactly(array);
    }

    @Test
    public void writeAttributeFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();
        when(view.write(anyString(), any())).thenThrow(exception);

        try {
            handler.writeAttribute(ATTR_NAME, new byte[0]);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test
    public void writeAttributeByteBufferSuccessTest()
        throws IOException
    {
        final byte[] array = new byte[ATTR_SIZE];

        //noinspection UnsecureRandomNumberGeneration
        new Random(System.currentTimeMillis()).nextBytes(array);

        final ByteBuffer buf = ByteBuffer.wrap(array);

        handler.writeAttribute(ATTR_NAME, buf);

        verify(view, only()).write(eq(ATTR_NAME), same(buf));
    }

    @Test
    public void writeAttributeByteArraySuccessTest()
        throws IOException
    {
        final byte[] array = new byte[ATTR_SIZE];

        //noinspection UnsecureRandomNumberGeneration
        new Random(System.currentTimeMillis()).nextBytes(array);

        final ArgumentCaptor<ByteBuffer> captor
            = ArgumentCaptor.forClass(ByteBuffer.class);

        handler.writeAttribute(ATTR_NAME, array);

        verify(view).write(eq(ATTR_NAME), captor.capture());

        final ByteBuffer buf = captor.getValue();

        assertThat(buf.array()).containsExactly(array);
    }
}
