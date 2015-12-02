package com.github.fge.jsr203.filestore;

import java.nio.file.attribute.FileAttributeView;

public class TestFileStore
    extends AbstractFileStore
{
    @Override
    public String name()
    {
        // TODO
        return null;
    }

    @Override
    public String type()
    {
        // TODO
        return null;
    }

    @Override
    public boolean isReadOnly()
    {
        // TODO
        return false;
    }

    @Override
    public boolean supportsFileAttributeView(
        final Class<? extends FileAttributeView> type)
    {
        // TODO
        return false;
    }

    @Override
    public boolean supportsFileAttributeView(final String name)
    {
        // TODO
        return false;
    }
}
