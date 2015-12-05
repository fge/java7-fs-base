package com.github.fge.jsr203.attrs.user;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public final class FailedUserDefinedFileAttributeView
    extends FailedFileAttributeView
    implements UserDefinedFileAttributeViewBase
{
    public FailedUserDefinedFileAttributeView(final IOException exception)
    {
        super(exception);
    }

    @Override
    public List<String> list()
        throws IOException
    {
        throw exception;
    }

    @Override
    public int size(final String name)
        throws IOException
    {
        throw exception;
    }

    @Override
    public int read(final String name, final ByteBuffer dst)
        throws IOException
    {
        throw exception;
    }

    @Override
    public int write(final String name, final ByteBuffer src)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void delete(final String name)
        throws IOException
    {
        throw exception;
    }
}
