package com.github.fge.jsr203.attrs.user;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public interface UserDefinedFileAttributeViewBase
    extends UserDefinedFileAttributeView
{
    @Override
    default String name()
    {
        return "user";
    }

    @Override
    default int write(final String name, final ByteBuffer src)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void delete(final String name)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
