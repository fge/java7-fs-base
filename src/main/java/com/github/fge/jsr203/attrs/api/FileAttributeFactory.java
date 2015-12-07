package com.github.fge.jsr203.attrs.api;

import com.github.fge.jsr203.attrs.api.byname.NamedAttributeDispatcher;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

public interface FileAttributeFactory
{
    <V extends FileAttributeView> V getViewByClass(Class<V> viewClass,
        Path path, LinkOption... options);

    <A extends BasicFileAttributes> A getAttributesByClass(
        Class<A> attributeClass, Path path, LinkOption... options)
        throws IOException;

    <V extends FileAttributeView> FileAttributeViewProvider<V>
        getFileAttributeViewProvider(Class<V> viewClass);

    <V extends FileAttributeView> Class<V> getViewClassForName(String viewName);

    <V extends FileAttributeView> NamedAttributeDispatcher<V>
        getDispatcherForView(V view);
}
