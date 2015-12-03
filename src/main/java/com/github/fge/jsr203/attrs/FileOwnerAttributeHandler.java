package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.FileOwnerAttributeView;

public class FileOwnerAttributeHandler<V extends FileOwnerAttributeView>
    extends FileAttributeHandler<V>
{
    public FileOwnerAttributeHandler(final V view)
    {
        super(view);
        addReader(StandardAttributeNames.OWNER, view::getOwner);
        addWriter(StandardAttributeNames.OWNER, view::setOwner);
    }
}
