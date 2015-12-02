package com.github.fge.jsr203.fs;

import com.github.fge.jsr203.filestore.AbstractFileStore;
import com.github.fge.jsr203.provider.AbstractFileSystemProvider;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Basic {@link FileSystem} implementation
 *
 * <p>This basic implementation enforces that only a single {@link FileStore}
 * exists.</p>
 *
 * <p>By default, this implementation also defines {@link
 * #getUserPrincipalLookupService()} and {@link #newWatchService()} to throw an
 * {@link UnsupportedOperationException}.</p>
 */
public abstract class AbstractFileSystem
    extends FileSystem
{
    private final AtomicBoolean closed = new AtomicBoolean(false);

    protected final AbstractFileStore fileStore;
    protected final AbstractFileSystemProvider provider;

    protected AbstractFileSystem(final AbstractFileStore fileStore,
        final AbstractFileSystemProvider provider)
    {
        this.fileStore = fileStore;
        this.provider = provider;
    }

    @Override
    public final FileSystemProvider provider()
    {
        return provider;
    }

    @Override
    public final void close()
        throws IOException
    {
        if (!closed.getAndSet(true))
            doClose();
    }

    protected abstract void doClose()
        throws IOException;

    @Override
    public final boolean isOpen()
    {
        return !closed.get();
    }

    @Override
    public final Iterable<FileStore> getFileStores()
    {
        return Collections.singleton(fileStore);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public WatchService newWatchService()
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
