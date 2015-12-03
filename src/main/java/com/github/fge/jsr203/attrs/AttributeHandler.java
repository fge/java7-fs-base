package com.github.fge.jsr203.attrs;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;

public abstract class AttributeHandler<V extends FileAttributeView>
{
    protected final V view;

    protected AttributeHandler(final V view)
    {
        this.view = view;
    }

    public final String getViewName()
    {
        return view.name();
    }

    public final V getView()
    {
        return view;
    }

    public abstract void writeAttribute(String name, Object value)
        throws IOException;

    public abstract Object readAttribute(String name)
        throws IOException;
}
