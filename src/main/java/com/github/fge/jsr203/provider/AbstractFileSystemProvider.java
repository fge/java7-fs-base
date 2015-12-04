package com.github.fge.jsr203.provider;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.attrs.factory.AttributeViewFactory;
import com.github.fge.jsr203.driver.FileSystemDriver;
import com.github.fge.jsr203.fs.AbstractFileSystem;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.ClosedFileSystemException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("OverloadedVarargsMethod")
public abstract class AbstractFileSystemProvider
    extends FileSystemProvider
{
    @VisibleForTesting
    final ConcurrentMap<AbstractFileSystem, FileSystemDriver> drivers
        = new ConcurrentHashMap<>();

    @Override
    public final void setAttribute(final Path path, final String attribute,
        final Object value, final LinkOption... options)
        throws IOException
    {
        final String viewName;
        final String attrName;

        final int index = Objects.requireNonNull(attribute).indexOf(';');

        if (index == -1) {
            viewName = StandardAttributeViewNames.BASIC;
            attrName = attribute;
        } else {
            viewName = attribute.substring(0, index);
            attrName = attribute.substring(index + 1);
        }

        final FileSystemDriver driver = getDriverForPath(path);
        final AttributeViewFactory factory = driver.getViewFactory();

        final Class<? extends FileAttributeView> viewClass
            = factory.getViewClassByName(viewName);

        if (viewClass == null)
            throw new UnsupportedOperationException();

    }

    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes,
        final LinkOption... options)
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        // TODO
        return null;
    }

    private FileSystemDriver getDriverForPath(final Path path)
    {
        @SuppressWarnings("CastToConcreteClass")
        final AbstractFileSystem fs = (AbstractFileSystem) path.getFileSystem();

        if (!fs.isOpen())
            throw new ClosedFileSystemException();

        return Optional.ofNullable(drivers.get(fs))
            .orElseThrow(ClosedFileSystemException::new);
    }
}
