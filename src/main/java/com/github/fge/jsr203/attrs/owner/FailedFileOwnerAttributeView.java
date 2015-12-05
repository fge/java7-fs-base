package com.github.fge.jsr203.attrs.owner;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;

public final class FailedFileOwnerAttributeView
    extends FailedFileAttributeView
    implements FileOwnerAttributeViewBase
{
    public FailedFileOwnerAttributeView(final IOException exception)
    {
        super(exception);
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
}
