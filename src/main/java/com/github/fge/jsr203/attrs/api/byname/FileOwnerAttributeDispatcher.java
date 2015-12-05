package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;

import java.nio.file.attribute.FileOwnerAttributeView;

public class FileOwnerAttributeDispatcher<V extends FileOwnerAttributeView>
    extends DiscreteNamedAttributeDispatcher<V>
{
    public FileOwnerAttributeDispatcher(final V view)
    {
        super(view);
        registerReader(StandardAttributeNames.OWNER, view::getOwner);
        registerWriter(StandardAttributeNames.OWNER, view::setOwner);
    }
}
