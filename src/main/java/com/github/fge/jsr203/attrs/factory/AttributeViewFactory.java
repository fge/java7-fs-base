package com.github.fge.jsr203.attrs.factory;

import java.nio.file.Path;
import java.nio.file.attribute.FileAttributeView;
import java.util.Set;

public interface AttributeViewFactory
{
    Class<? extends FileAttributeView> getViewClassByName(String name);

    <V extends FileAttributeView> V getView(Class<V> viewClass, Path path);

    boolean supportsViewClass(Class<? extends FileAttributeView> viewClass);

    Set<String> getViewNames();
}
