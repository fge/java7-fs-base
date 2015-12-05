package com.github.fge.jsr203.attrs.api.byname;

import java.io.IOException;

@FunctionalInterface
public interface NamedAttributeReader
{
    Object read()
        throws IOException;
}
