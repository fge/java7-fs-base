package com.github.fge.jsr203.attrs.basic;

import java.nio.file.attribute.BasicFileAttributeView;

public interface BasicFileAttributeViewBase
    extends BasicFileAttributeView
{
    @Override
    default String name()
    {
        return "basic";
    }
}
