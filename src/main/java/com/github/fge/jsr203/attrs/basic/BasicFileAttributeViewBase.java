package com.github.fge.jsr203.attrs.basic;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

@FunctionalInterface
public interface BasicFileAttributeViewBase
    extends BasicFileAttributeView
{
    @Override
    default String name()
    {
        return "basic";
    }

    @Override
    default void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
