package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.basic.BasicFileAttributeViewBase;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;

@FunctionalInterface
public interface DosFileAttributeViewBase
    extends DosFileAttributeView, BasicFileAttributeViewBase
{
    @Override
    default String name()
    {
        return "dos";
    }

    @Override
    default void setReadOnly(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setHidden(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setSystem(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setArchive(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
