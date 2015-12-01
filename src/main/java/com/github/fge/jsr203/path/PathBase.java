package com.github.fge.jsr203.path;

import com.github.fge.jsr203.attrs.basic.BasicFileAttributesBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

/**
 * An extesion of the {@link Path} interface with default implementations
 *
 * <p>The default implementations are those implemented in Java 9.</p>
 *
 * <p>Two additional methods of the interface have default implementations:</p>
 *
 * <ul>
 *     <li>{@link #toFile()} will always throw {@link
 *     UnsupportedOperationException}: it is not expected that implemented
 *     filesystems will be the {@link FileSystems#getDefault() default
 *     filesystem};</li>
 *     <li>{@link #toRealPath(LinkOption...)} is simply a call to {@link
 *     #toAbsolutePath()}: implemented filesystems are not expected to support
 *     symbolic links (see also {@link
 *     BasicFileAttributesBase#isSymbolicLink()}).</li>
 * </ul>
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

    @Override
    default Path toRealPath(final LinkOption... options)
        throws IOException
    {
        return toAbsolutePath();
    }

    @Override
    default File toFile()
    {
        throw new UnsupportedOperationException();
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
    default boolean startsWith(final String other)
    {
        return startsWith(getFileSystem().getPath(other));
    }
}
