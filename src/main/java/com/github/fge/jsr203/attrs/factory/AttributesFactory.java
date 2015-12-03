package com.github.fge.jsr203.attrs.factory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public interface AttributesFactory
{
    Class<? extends FileAttributeView> getViewClassByName(String name);

    <V extends FileAttributeView> V getView(Class<V> viewClass, Path path);

    <A extends BasicFileAttributes> A getAttributes(Class<A> attributesClass,
        Path path)
        throws IOException;
}
