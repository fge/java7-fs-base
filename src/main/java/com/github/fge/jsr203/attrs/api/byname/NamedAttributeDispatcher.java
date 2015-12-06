package com.github.fge.jsr203.attrs.api.byname;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Map;

public abstract class NamedAttributeDispatcher<V extends FileAttributeView>
{
    protected final V view;

    protected NamedAttributeDispatcher(final V view)
    {
        this.view = view;
    }

    public abstract Object readByName(String name)
        throws IOException;

    public abstract void writeByBame(String name, Object value)
        throws IOException;

    public abstract Map<String, Object> readAllAttributes()
        throws IOException;
}
