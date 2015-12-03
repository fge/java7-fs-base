package com.github.fge.jsr203.attrs;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

@FunctionalInterface
public interface AttributesProvider<A extends BasicFileAttributes>
{
    A getAttributes()
        throws IOException;
}
