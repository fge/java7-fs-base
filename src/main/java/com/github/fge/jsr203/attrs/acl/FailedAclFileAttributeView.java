package com.github.fge.jsr203.attrs.acl;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public final class FailedAclFileAttributeView
    extends FailedFileAttributeView
    implements AclFileAttributeViewBase
{
    public FailedAclFileAttributeView(final IOException exception)
    {
        super(exception);
    }

    @Override
    public List<AclEntry> getAcl()
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
    public void setAcl(final List<AclEntry> acl)
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
}
