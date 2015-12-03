package com.github.fge.jsr203.attrs.owner;

import com.github.fge.jsr203.attrs.FixedNamesAttributeHandler;
import com.github.fge.jsr203.attrs.StandardAttributeNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class FileOwnerAttributeHandlerTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileOwnerAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FixedNamesAttributeHandler<?> handler;

    @BeforeMethod
    public void initHandler()
    {
        view = mock(FileOwnerAttributeView.class);
        handler = new FileOwnerAttributeHandler<>(view);
    }

    @Test
    public void readOwnerTest()
        throws IOException
    {
        final UserPrincipal expected = mock(UserPrincipal.class);
        when(view.getOwner()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.OWNER);

        assertThat(actual).isSameAs(expected);

        verify(view, only()).getOwner();
    }

    @Test
    public void writeOwnerTest()
        throws IOException
    {
        final UserPrincipal principal = mock(UserPrincipal.class);

        handler.writeAttribute(StandardAttributeNames.OWNER, principal);

        verify(view, only()).setOwner(same(principal));
    }
}

