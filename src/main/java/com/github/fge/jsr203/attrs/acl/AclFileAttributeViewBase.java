package com.github.fge.jsr203.attrs.acl;

import com.github.fge.jsr203.attrs.owner.FileOwnerAttributeViewBase;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.List;

public interface AclFileAttributeViewBase
    extends AclFileAttributeView, FileOwnerAttributeViewBase
{
    @Override
    default String name()
    {
        return "acl";
    }

    @Override
    default void setAcl(final List<AclEntry> acl)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
