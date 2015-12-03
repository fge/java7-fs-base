package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.StandardAttributeNames;

import java.nio.file.attribute.FileOwnerAttributeView;

public final class FileOwnerAttributeHandler
    extends FileAttributeHandler<FileOwnerAttributeView>
{
    public FileOwnerAttributeHandler(final FileOwnerAttributeView view)
    {
        super(view);
        addReader(StandardAttributeNames.OWNER, view::getOwner);
        addWriter(StandardAttributeNames.OWNER, view::setOwner);
    }
}
