package com.github.fge.jsr203.attrs.acl;

import com.github.fge.jsr203.attrs.owner.FileOwnerAttributeHandler;
import com.github.fge.jsr203.attrs.StandardAttributeNames;

import java.nio.file.attribute.AclFileAttributeView;

public class AclAttributeHandler<V extends AclFileAttributeView>
    extends FileOwnerAttributeHandler<V>
{
    public AclAttributeHandler(final V view)
    {
        super(view);
        addReader(StandardAttributeNames.ACL, view::getAcl);
        addWriter(StandardAttributeNames.ACL, view::setAcl);
    }
}
