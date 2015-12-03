package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.AttributeReader;
import com.github.fge.jsr203.AttributeWriter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ProhibitedExceptionCaught")
public final class FileAttributeHandlerTest
{
    private static final String NAME = "foo";

    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileAttributeHandler<?> handler;

    @BeforeMethod
    public void init()
    {
        view = mock(FileAttributeView.class);
        when(view.name()).thenReturn(NAME);

        handler = new TestFileAttributeHandler(view);
    }

    @Test
    public void initTest()
    {
        assertThat(handler.getView()).isSameAs(view);
        assertThat(handler.getViewName()).isSameAs(NAME);

        verify(view, only()).name();
    }

    @Test
    public void illegalReaderRegisterTest()
    {
        final AttributeReader reader = mock(AttributeReader.class);

        try {
            handler.addReader(null, reader);
            shouldHaveThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage(FileAttributeHandler.NULL_ATTR_NAME);
        }

        try {
            handler.addReader(NAME, null);
            shouldHaveThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage(FileAttributeHandler.NULL_READER);
        }

        handler.addReader(NAME, reader);

        try {
            handler.addReader(NAME, reader);
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            final String msg
                = String.format(FileAttributeHandler.DUPLICATE_READER, NAME);
            assertThat(e).hasMessage(msg);
        }
    }

    @Test
    public void readAttributeTest()
        throws IOException
    {
        final Object expected = mock(Object.class);
        final AttributeReader reader = mock(AttributeReader.class);
        when(reader.read()).thenReturn(expected);

        handler.addReader(NAME, reader);

        final Object actual = handler.readAttribute(NAME);

        assertThat(actual).isSameAs(expected);
        verify(reader, only()).read();
    }

    @Test
    public void illegalWriterRegisterTest()
    {
        final AttributeWriter<?> writer = mock(AttributeWriter.class);

        try {
            handler.addWriter(null, writer);
            shouldHaveThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage(FileAttributeHandler.NULL_ATTR_NAME);
        }

        try {
            handler.addWriter(NAME, null);
            shouldHaveThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage(FileAttributeHandler.NULL_WRITER);
        }

        handler.addWriter(NAME, writer);

        try {
            handler.addWriter(NAME, writer);
            shouldHaveThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            final String msg
                = String.format(FileAttributeHandler.DUPLICATE_WRITER, NAME);
            assertThat(e).hasMessage(msg);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void writeAttributeTest()
        throws IOException
    {
        final Object value = mock(Object.class);
        final AttributeWriter writer = mock(AttributeWriter.class);

        handler.addWriter(NAME, writer);

        handler.writeAttribute(NAME, value);

        verify(writer, only()).write(same(value));
    }
}
