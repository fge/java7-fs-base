package com.github.fge.jsr203.attrs.api.byname;

import java.io.IOException;

@FunctionalInterface
public interface NamedAttributeWriter<T>
{
    void write(T value)
        throws IOException;
}
