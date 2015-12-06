package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserDefinedAttributeDispatcher<V extends UserDefinedFileAttributeView>
    extends NamedAttributeDispatcher<V>
{
    @VisibleForTesting
    static final String NO_SUCH_ATTRIBUTE
        = "view has no attribute with name '%s'";

    public UserDefinedAttributeDispatcher(final V view)
    {
        super(view);
    }

    @Override
    public Object readByName(final String name)
        throws IOException
    {
        if (!view.list().contains(name))
            throw new IOException(String.format(NO_SUCH_ATTRIBUTE, name));

        final ByteBuffer buf = ByteBuffer.allocate(view.size(name));
        view.read(name, buf);
        buf.flip();
        return buf;
    }

    @Override
    public void writeByBame(final String name, final Object value)
        throws IOException
    {
        final ByteBuffer buf = value instanceof ByteBuffer
            ? (ByteBuffer) value
            : ByteBuffer.wrap((byte[]) value);
        view.write(name, buf);

    }

    // TODO: not tested :/
    @Override
    public Map<String, Object> readAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        for (final String name: view.list())
            map.put(name, readByName(name));

        return Collections.unmodifiableMap(map);
    }
}
