package com.github.fge.jsr203.attrs.user;

import com.github.fge.jsr203.attrs.AttributeHandler;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;

public class UserDefinedAttributeHandler
    extends AttributeHandler<UserDefinedFileAttributeView>
{
    @VisibleForTesting
    static final String NO_SUCH_ATTRIBUTE = "no such attribute '%s'";

    public UserDefinedAttributeHandler(
        final UserDefinedFileAttributeView view)
    {
        super(view);
    }

    @Override
    public void writeAttribute(final String name, final Object value)
        throws IOException
    {
        final ByteBuffer buf = value instanceof ByteBuffer
            ? (ByteBuffer) value
            : ByteBuffer.wrap((byte[]) value);
        view.write(name, buf);
    }

    @Override
    public Object readAttribute(final String name)
        throws IOException
    {
        if (!view.list().contains(name))
            throw new IOException(String.format(NO_SUCH_ATTRIBUTE, name));

        final ByteBuffer buf = ByteBuffer.allocate(view.size(name));
        view.read(name, buf);
        buf.flip();
        return buf;
    }
}
