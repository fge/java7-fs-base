package com.github.fge.jsr203.fs;

import com.github.fge.jsr203.filestore.AbstractFileStore;
import com.github.fge.jsr203.provider.AbstractFileSystemProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Set;

public class TestFileSystem
    extends AbstractFileSystem
{
    protected TestFileSystem(final AbstractFileStore fileStore,
        final AbstractFileSystemProvider provider)
    {
        super(fileStore, provider);
    }

    @Override
    protected void doClose()
        throws IOException
    {
        // TODO

    }

    @Override
    public boolean isReadOnly()
    {
        // TODO
        return false;
    }

    @Override
    public String getSeparator()
    {
        // TODO
        return null;
    }

    @Override
    public Iterable<Path> getRootDirectories()
    {
        // TODO
        return null;
    }

    @Override
    public Set<String> supportedFileAttributeViews()
    {
        // TODO
        return null;
    }

    @Override
    public Path getPath(final String first, final String... more)
    {
        // TODO
        return null;
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern)
    {
        // TODO
        return null;
    }
}
