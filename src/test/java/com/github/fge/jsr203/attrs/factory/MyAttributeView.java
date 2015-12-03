package com.github.fge.jsr203.attrs.factory;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;

public interface MyAttributeView
    extends FileAttributeView
{
    MyAttributes readAttributes()
        throws IOException;
}
