package com.github.fge.jsr203.attrs.posix;

import com.github.fge.jsr203.attrs.FixedNamesAttributeHandler;
import com.github.fge.jsr203.attrs.StandardAttributeNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PosixAttributeHandlerTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private PosixFileAttributes attributes;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private PosixFileAttributeView view;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FixedNamesAttributeHandler<?> handler;

    @BeforeMethod
    public void initHandler()
        throws IOException
    {
        attributes = mock(PosixFileAttributes.class);
        view = mock(PosixFileAttributeView.class);
        when(view.readAttributes()).thenReturn(attributes);
        handler = new PosixAttributeHandler<>(view);
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
    public void readGroupTest()
        throws IOException
    {
        final GroupPrincipal expected = mock(GroupPrincipal.class);
        when(attributes.group()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.GROUP);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).group();
    }

    @Test
    public void readPermissionsTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final Set<PosixFilePermission> expected = mock(Set.class);
        when(attributes.permissions()).thenReturn(expected);

        final Object actual
            = handler.readAttribute(StandardAttributeNames.PERMISSIONS);

        assertThat(actual).isSameAs(expected);

        verify(attributes, only()).permissions();
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
    public void writeGroupTest()
        throws IOException
    {
        final GroupPrincipal principal = mock(GroupPrincipal.class);

        handler.writeAttribute(StandardAttributeNames.GROUP, principal);

        verify(view, only()).setGroup(same(principal));
    }

    @Test
    public void writePermissionsTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final Set<PosixFilePermission> permissions = mock(Set.class);

        handler.writeAttribute(StandardAttributeNames.PERMISSIONS, permissions);

        verify(view, only()).setPermissions(permissions);
    }
}
