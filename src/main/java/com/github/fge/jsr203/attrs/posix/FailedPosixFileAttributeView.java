package com.github.fge.jsr203.attrs.posix;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

public final class FailedPosixFileAttributeView
    extends FailedFileAttributeView
    implements PosixFileAttributeViewBase
{
    public FailedPosixFileAttributeView(final IOException exception)
    {
        super(exception);
    }

    @Override
    public PosixFileAttributes readAttributes()
        throws IOException
    {
        throw exception;
    }

    @Override
    public UserPrincipal getOwner()
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setOwner(final UserPrincipal owner)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setPermissions(final Set<PosixFilePermission> perms)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setGroup(final GroupPrincipal group)
        throws IOException
    {
        throw exception;
    }
}
