package com.github.fge.jsr203.attrs.api;

import com.github.fge.jsr203.attrs.api.byname.NamedAttributeDispatcher;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public interface FileAttributeFactory
{
    <A extends BasicFileAttributes> FileAttributesProvider<A>
        getAttributesProvider(Class<A> attributesClass);

    <V extends FileAttributeView> FileAttributeViewProvider<V>
        getFileAttributeViewProvider(Class<V> viewClass);

    <V extends FileAttributeView> Class<V> getViewClassForName(String viewName);

    <V extends FileAttributeView> NamedAttributeDispatcher
        getDispatcherForView(V view);

    <V extends FileAttributeView> V getOnFailure(Class<V> viewClass,
        IOException exception);
}
