package com.github.fge.jsr203.attrs.factory;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public interface AttributesFactory
{
    <A extends BasicFileAttributes, V extends FileAttributeView> A
        getAttributesFromView(V view, Class<A> attributesClass)
        throws IOException;
}
