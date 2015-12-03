package com.github.fge.jsr203.attrs.factory;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public interface AttributesFactory
{
    /*
     * Plan:
     *
     * 1. obtain the view name from the attributes class;
     * 2. obtain the view class from the view name;
     * 3. obtain the view instance from the view class and path;
     * 4. obtain the attributes instance from the view instance
     */
    <A extends BasicFileAttributes> String getViewNameForAttributesClass(
        Class<A> attributesClass);

    <A extends BasicFileAttributes, V extends FileAttributeView> A
        getAttributesFromView(V view);
}
