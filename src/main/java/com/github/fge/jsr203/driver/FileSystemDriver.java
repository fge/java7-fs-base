package com.github.fge.jsr203.driver;

import com.github.fge.jsr203.attrs.factory.AttributeViewFactory;
import com.github.fge.jsr203.attrs.factory.AttributesFactory;

public interface FileSystemDriver
{
    AttributeViewFactory getViewFactory();

    AttributesFactory getAttributesFactory();
}
