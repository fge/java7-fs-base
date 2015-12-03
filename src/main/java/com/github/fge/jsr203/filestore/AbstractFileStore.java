package com.github.fge.jsr203.filestore;

import com.github.fge.jsr203.fs.AbstractFileSystem;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.attribute.FileStoreAttributeView;

/**
 * Basic abstract implementation of a {@link FileStore}
 *
 * <p>This basic implementation defines default for the following methods:</p>
 *
 * <ul>
 *     <li>all methods returning a number of available bytes return {@link
 *     Long#MAX_VALUE};</li>
 *     <li>{@link #getAttribute(String)} throws {@link
 *     UnsupportedOperationException};</li>
 *     <li>{@link #getFileStoreAttributeView(Class)} returns null.</li>
 * </ul>
 *
 * <p>Noteworthy is that the {@link #AbstractFileStore() default constructor}
 * will provide a <em>read only</em> file store by default. This is quite
 * important since the provided default filesystem base class ({@link
 * AbstractFileSystem}) relies on this class's {@link #isReadOnly()} to tell
 * whether the implemented file system is also read only (see {@link
 * FileSystem#isReadOnly()}).</p>
 */
public abstract class AbstractFileStore
    extends FileStore
{
    private final boolean readOnly;

    protected AbstractFileStore(final boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    protected AbstractFileStore()
    {
        this(true);
    }

    @Override
    public final boolean isReadOnly()
    {
        return readOnly;
    }

    @Override
    public long getTotalSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUnallocatedSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUsableSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(
        final Class<V> type)
    {
        return null;
    }

    @Override
    public Object getAttribute(final String attribute)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
