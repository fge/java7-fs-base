package com.github.fge.jsr203.attrs.owner;

import com.github.fge.jsr203.attrs.FixedNamesAttributeHandler;
import com.github.fge.jsr203.attrs.StandardAttributeNames;

import java.nio.file.attribute.FileOwnerAttributeView;

public class FileOwnerAttributeHandler<V extends FileOwnerAttributeView>
    extends FixedNamesAttributeHandler<V>
{
    public FileOwnerAttributeHandler(final V view)
    {
        super(view);
        addReader(StandardAttributeNames.OWNER, view::getOwner);
        addWriter(StandardAttributeNames.OWNER, view::setOwner);
    }
}
