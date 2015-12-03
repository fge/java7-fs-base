package com.github.fge.jsr203;

import java.io.IOException;

@FunctionalInterface
public interface AttributeWriter<T>
{
    void write(T value)
        throws IOException;
}
