package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributeHandler;

import java.nio.file.attribute.FileAttributeView;

public interface AttributeHandlerFactory
{
    <V extends FileAttributeView> AttributeHandler<V> getHandlerForView(V view);
}
