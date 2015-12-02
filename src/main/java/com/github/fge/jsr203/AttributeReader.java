package com.github.fge.jsr203;

import java.io.IOException;

@FunctionalInterface
public interface AttributeReader
{
    Object read()
        throws IOException;
}
