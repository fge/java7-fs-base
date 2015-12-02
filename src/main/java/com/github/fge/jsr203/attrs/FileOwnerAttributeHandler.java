package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.FileOwnerAttributeView;

public final class FileOwnerAttributeHandler
    extends FileAttributeHandler<FileOwnerAttributeView>
{
    public FileOwnerAttributeHandler(final FileOwnerAttributeView view)
    {
        super(view);
    }
}
