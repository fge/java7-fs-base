package com.github.fge.jsr203.attrs.api.byname;

import java.io.IOException;
import java.util.Map;

public interface NamedAttributeDispatcher
{
    Object readByName(String name)
        throws IOException;

    void writeByBame(String name, Object value)
        throws IOException;

    Map<String, Object> readAllAttributes()
        throws IOException;
}
