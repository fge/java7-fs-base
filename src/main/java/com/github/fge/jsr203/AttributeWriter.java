package com.github.fge.jsr203;

import java.io.IOException;

@FunctionalInterface
public interface AttributeWriter
{
    void write(Object value)
        throws IOException;
}
