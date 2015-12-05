package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;
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

@SuppressWarnings("InstanceVariableMayNotBeInitialized")
public class FileOwnerAttributeDispatcherTest
{
    private FileOwnerAttributeView view;
    private NamedAttributeDispatcher dispatcher;

    @BeforeMethod
    public void initDispatcher()
    {
        view = mock(FileOwnerAttributeView.class);
        dispatcher = new FileOwnerAttributeDispatcher<>(view);
    }

    @Test
    public void readOwnerTest()
        throws IOException
    {
        final UserPrincipal expected = mock(UserPrincipal.class);
        when(view.getOwner()).thenReturn(expected);

        final Object actual
            = dispatcher.readByName(StandardAttributeNames.OWNER);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    public void writeOwnerTest()
        throws IOException
    {
        final UserPrincipal principal = mock(UserPrincipal.class);

        dispatcher.writeByBame(StandardAttributeNames.OWNER,
            principal);

        verify(view, only()).setOwner(same(principal));
    }
}
