package com.github.fge.jsr203.path;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

/**
 * An extesion of the {@link Path} interface with default implementations
 *
 * <p>Note that these default methods will be implemented in Java 9.</p>
 *
 * <p>There is one fundamental difference: in this interface, {@link #toFile()}
 * always throws an {@link UnsupportedOperationException} since we do not
 * implement the default filesystem.</p>
 */
public interface PathBase
    extends Path
{
    @Override
    default boolean endsWith(final String other)
    {
        return endsWith(getFileSystem().getPath(other));
    }

    @Override
    default Path resolve(final String other)
    {
        return resolve(getFileSystem().getPath(other));
    }

    @Override
    default Path resolveSibling(final Path other)
    {
        if (other == null)
            throw new NullPointerException();
        final Path parent = getParent();
        return parent == null ? other : parent.resolve(other);
    }

    @Override
    default Path resolveSibling(final String other)
    {
        return resolveSibling(getFileSystem().getPath(other));
    }

    @SuppressWarnings("ProblematicVarargsMethodOverride")
    @Override
    default WatchKey register(final WatchService watcher,
        final WatchEvent.Kind<?>[] events)
        throws IOException
    {
        return register(watcher, events,  new WatchEvent.Modifier[0]);
    }

    @Override
    default Iterator<Path> iterator()
    {
        return new PathIterator(this);
    }

    @Override
    default File toFile()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean startsWith(final String other)
    {
        return startsWith(getFileSystem().getPath(other));
    }
}
