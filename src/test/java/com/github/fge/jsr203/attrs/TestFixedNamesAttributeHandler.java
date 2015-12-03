package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.FileAttributeView;

final class TestFixedNamesAttributeHandler
    extends FixedNamesAttributeHandler<FileAttributeView>
{
    TestFixedNamesAttributeHandler(final FileAttributeView view)
    {
        super(view);
    }
}
