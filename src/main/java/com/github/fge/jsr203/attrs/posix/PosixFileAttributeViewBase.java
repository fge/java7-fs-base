package com.github.fge.jsr203.attrs.posix;

import com.github.fge.jsr203.attrs.basic.BasicFileAttributeViewBase;
import com.github.fge.jsr203.attrs.owner.FileOwnerAttributeViewBase;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

@FunctionalInterface
public interface PosixFileAttributeViewBase
    extends PosixFileAttributeView, BasicFileAttributeViewBase,
    FileOwnerAttributeViewBase
{
    @Override
    default UserPrincipal getOwner()
        throws IOException
    {
        return readAttributes().owner();
    }

    @Override
    default String name()
    {
        return "posix";
    }

    @Override
    default void setPermissions(final Set<PosixFilePermission> perms)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setGroup(final GroupPrincipal group)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
