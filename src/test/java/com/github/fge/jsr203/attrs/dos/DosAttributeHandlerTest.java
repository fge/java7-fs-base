package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.FixedNamesAttributeHandler;
import com.github.fge.jsr203.attrs.StandardAttributeNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DosAttributeHandlerTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private DosFileAttributes attributes;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private DosFileAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FixedNamesAttributeHandler<?> handler;

    @BeforeMethod
    public void initHandler()
        throws IOException
    {
        attributes = mock(DosFileAttributes.class);
        view = mock(DosFileAttributeView.class);
        when(view.readAttributes()).thenReturn(attributes);
        handler = new DosAttributeHandler<>(view);
    }

    @Test
    public void readReadOnlyTest()
        throws IOException
    {
        when(attributes.isReadOnly()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.READONLY);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isReadOnly();
    }

    @Test
    public void readHiddenTest()
        throws IOException
    {
        when(attributes.isHidden()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.HIDDEN);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isHidden();
    }

    @Test
    public void readSystemTest()
        throws IOException
    {
        when(attributes.isSystem()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.SYSTEM);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isSystem();
    }

    @Test
    public void readArchiveTest()
        throws IOException
    {
        when(attributes.isArchive()).thenReturn(true);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.ARCHIVE);

        assertThat(actual).isEqualTo(true);

        verify(attributes, only()).isArchive();
    }

    @Test
    public void writeReadOnlyTest()
        throws IOException
    {
        handler.writeAttribute(StandardAttributeNames.READONLY, true);

        verify(view, only()).setReadOnly(true);
    }

    @Test
    public void writeHiddenTest()
        throws IOException
    {
        handler.writeAttribute(StandardAttributeNames.HIDDEN, true);

        verify(view, only()).setHidden(true);
    }

    @Test
    public void writeSystemTest()
        throws IOException
    {
        handler.writeAttribute(StandardAttributeNames.SYSTEM, true);

        verify(view, only()).setSystem(true);
    }

    @Test
    public void writeArchiveTest()
        throws IOException
    {
        handler.writeAttribute(StandardAttributeNames.ARCHIVE, true);

        verify(view, only()).setArchive(true);
    }
}
