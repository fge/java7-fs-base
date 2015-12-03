package com.github.fge.jsr203.attrs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

@FunctionalInterface
public interface AttributesProvider<V extends FileAttributeView, A extends BasicFileAttributes>
{
    A getAttributes(V view)
        throws IOException;
}
