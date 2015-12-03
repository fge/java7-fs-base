package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public abstract class FileAttributeHandlerWithAttributes<V extends FileAttributeView, A extends BasicFileAttributes>
    extends FileAttributeHandler<V>
    implements AttributesProvider<A>
{
    protected FileAttributeHandlerWithAttributes(final V view)
    {
        super(view);
    }
}
