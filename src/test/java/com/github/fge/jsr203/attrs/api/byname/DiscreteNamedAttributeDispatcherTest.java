package com.github.fge.jsr203.attrs.api.byname;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class DiscreteNamedAttributeDispatcherTest
{
    private static final String ATTRNAME1 = "attr1";
    private static final String ATTRNAME2 = "attr2";

    private DiscreteNamedAttributeDispatcher<FileAttributeView> dispatcher;

    @BeforeMethod
    public void initDispatcher()
    {
        final FileAttributeView view = mock(FileAttributeView.class);
        dispatcher = new DiscreteNamedAttributeDispatcher<>(view);
    }

    @Test
    public void noReaderTest()
        throws IOException
    {
        try {
            dispatcher.readAttributeByName(ATTRNAME1);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                DiscreteNamedAttributeDispatcher.NO_SUCH_READER, ATTRNAME1
            ));
        }
    }

    @Test
    public void addReaderTest()
        throws IOException
    {
        final Object expected = new Object();

        final NamedAttributeReader reader = mock(NamedAttributeReader.class);
        when(reader.read()).thenReturn(expected);

        dispatcher.registerReader(ATTRNAME1, reader);

        final Object actual = dispatcher.readAttributeByName(ATTRNAME1);

        assertThat(actual).isSameAs(expected);
    }

    @Test(dependsOnMethods = "addReaderTest")
    public void illegalAddReaderTwiceTest()
    {
        final NamedAttributeReader reader1 = mock(NamedAttributeReader.class);
        final NamedAttributeReader reader2 = mock(NamedAttributeReader.class);

        dispatcher.registerReader(ATTRNAME1, reader1);
        try {
            dispatcher.registerReader(ATTRNAME1, reader2);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                DiscreteNamedAttributeDispatcher.READER_ALREADY_REGISTERED,
                ATTRNAME1
            ));
        }
    }

    @Test
    public void noWriterTest()
        throws IOException
    {
        try {
            dispatcher.setAttributeByName(ATTRNAME1, new Object());
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                DiscreteNamedAttributeDispatcher.NO_SUCH_WRITER, ATTRNAME1
            ));
        }
    }

    @Test
    public void addWriterTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final NamedAttributeWriter<Object> writer
            = mock(NamedAttributeWriter.class);

        dispatcher.registerWriter(ATTRNAME1, writer);

        final Object value = new Object();

        dispatcher.setAttributeByName(ATTRNAME1, value);

        verify(writer, only()).write(same(value));
    }

    @Test(dependsOnMethods = "addWriterTest")
    public void illegalAddWriterTwiceTest()
    {
        @SuppressWarnings("unchecked")
        final NamedAttributeWriter<Object> writer1
            = mock(NamedAttributeWriter.class);

        @SuppressWarnings("unchecked")
        final NamedAttributeWriter<Object> writer2
            = mock(NamedAttributeWriter.class);

        dispatcher.registerWriter(ATTRNAME1, writer1);

        try {
            dispatcher.registerWriter(ATTRNAME1, writer2);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                DiscreteNamedAttributeDispatcher.WRITER_ALREADY_REGISTERED,
                ATTRNAME1
            ));
        }
    }

    @Test(dependsOnMethods = "addReaderTest")
    public void readAllAttributesTest()
        throws IOException
    {
        final NamedAttributeReader reader1 = mock(NamedAttributeReader.class);
        final Object value1 = new Object();
        when(reader1.read()).thenReturn(value1);
        dispatcher.registerReader(ATTRNAME1, reader1);

        final NamedAttributeReader reader2 = mock(NamedAttributeReader.class);
        final Object value2 = new Object();
        when(reader2.read()).thenReturn(value2);
        dispatcher.registerReader(ATTRNAME2, reader2);

        final Map<String, Object> expected = new HashMap<>();
        expected.put(ATTRNAME1, value1);
        expected.put(ATTRNAME2, value2);

        final Map<String, Object> actual = dispatcher.readAllAttributes();

        assertThat(actual).isEqualTo(expected);
    }
}
