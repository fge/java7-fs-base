package com.github.fge.jsr203.attrs;

import java.io.IOException;

@FunctionalInterface
public interface AttributeWriter<T>
{
    void write(T value)
        throws IOException;
}
