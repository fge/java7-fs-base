package com.github.fge.jsr203.attrs;

import java.io.IOException;

@FunctionalInterface
public interface AttributeReader
{
    Object read()
        throws IOException;
}
