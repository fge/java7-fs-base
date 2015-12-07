package com.github.fge.jsr203.provider;

import com.github.fge.jsr203.attrs.api.FileAttributeFactory;
import com.github.fge.jsr203.attrs.api.FileAttributeViewProvider;
import com.github.fge.jsr203.attrs.api.byname.NamedAttributeDispatcher;
import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("OverloadedVarargsMethod")
public abstract class AbstractFileSystemProvider
    extends FileSystemProvider
{
    private static final Pattern COMMA = Pattern.compile(",");

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

        final FileAttributeFactory factory = getFileAttributeFactory(path);
        final Class<? extends FileAttributeView> viewClass
            = factory.getViewClassForName(viewName);
        final NamedAttributeDispatcher<? extends FileAttributeView> dispatcher
            = getDispatcher(viewClass, path, options);
        dispatcher.writeByBame(attrName, value);
    }

    @Override
    public Map<String, Object> readAttributes(final Path path,
        final String attributes, final LinkOption... options)
        throws IOException
    {
        final String viewName;
        final String attrSpec;

        final int index = Objects.requireNonNull(attributes).indexOf(';');

        if (index == -1) {
            viewName = StandardAttributeViewNames.BASIC;
            attrSpec = attributes;
        } else {
            viewName = attributes.substring(0, index);
            attrSpec = attributes.substring(index + 1);
        }

        final FileAttributeFactory factory = getFileAttributeFactory(path);
        final Class<? extends FileAttributeView> viewClass
            = factory.getViewClassForName(viewName);
        final NamedAttributeDispatcher<? extends FileAttributeView> dispatcher
            = getDispatcher(viewClass, path, options);

        if (StandardAttributeNames.ALL.equals(attrSpec))
            return dispatcher.readAllAttributes();

        final Map<String, Object> map = new HashMap<>();
        final Set<String> attrNames = COMMA.splitAsStream(attrSpec)
            .collect(Collectors.toSet());

        for (final String attrName: attrNames)
            map.put(attrName, dispatcher.readByName(attrName));

        return Collections.unmodifiableMap(map);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path,
        final Class<A> type, final LinkOption... options)
        throws IOException
    {
        final FileAttributeFactory factory = getFileAttributeFactory(path);
        return factory.getAttributesByClass(type, path, options);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path,
        final Class<V> type, final LinkOption... options)
    {
        final FileAttributeFactory factory = getFileAttributeFactory(path);
        return factory.getViewByClass(type, path, options);
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

    private FileAttributeFactory getFileAttributeFactory(final Path path)
    {
        final FileSystemDriver driver = getDriverForPath(path);
        return driver.getFileAttributeFactory();
    }

    private <V extends FileAttributeView> NamedAttributeDispatcher<V>
    getDispatcher(final Class<V> viewClass, final Path path,
        final LinkOption... options)
        throws IOException
    {
        final FileAttributeFactory factory = getFileAttributeFactory(path);
        final FileAttributeViewProvider<V> provider
            = factory.getFileAttributeViewProvider(viewClass);
        final V view = provider.getView(path, options);
        return factory.getDispatcherForView(view);
    }
}
