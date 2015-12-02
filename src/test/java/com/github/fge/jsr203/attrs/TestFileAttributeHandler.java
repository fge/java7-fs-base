package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.FileAttributeView;

final class TestFileAttributeHandler
    extends FileAttributeHandler<FileAttributeView>
{
    TestFileAttributeHandler(final FileAttributeView view)
    {
        super(view);
    }
}
