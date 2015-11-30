package com.github.fge.jsr203.attrs.owner;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

@FunctionalInterface
public interface FileOwnerAttributeViewBase
    extends FileOwnerAttributeView
{
    @Override
    default String name()
    {
        return "owner";
    }

    @Override
    default void setOwner(final UserPrincipal owner)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
