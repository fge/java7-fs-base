package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.StandardAttributeNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AclAttributeHandlerTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private AclFileAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileAttributeHandler<?> handler;

    @BeforeMethod
    public void initHandler()
    {
        view = mock(AclFileAttributeView.class);
        handler = new AclAttributeHandler(view);
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
    public void readAclTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<AclEntry> expected = mock(List.class);
        when(view.getAcl()).thenReturn(expected);

        final Object actual = handler.readAttribute(StandardAttributeNames.ACL);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    public void writeOwnerTest()
        throws IOException
    {
        final UserPrincipal principal = mock(UserPrincipal.class);

        handler.writeAttribute(StandardAttributeNames.OWNER, principal);

        verify(view, only()).setOwner(same(principal));
    }

    @Test
    public void writeAclTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<AclEntry> list = mock(List.class);

        handler.writeAttribute(StandardAttributeNames.ACL, list);

        verify(view, only()).setAcl(same(list));
    }
}
