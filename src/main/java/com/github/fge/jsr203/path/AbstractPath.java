package com.github.fge.jsr203.path;

import com.github.fge.jsr203.FileSystemMismatchException;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

/**
 * Basic implementation of a {@link Path}
 *
 * <p>This abstract class implements {@link PathBase} and does not override any
 * of its default implementations.</p>
 *
 * <p>It also enforces another very important constraint: all methods accepting
 * paths as arguments will check whether the <em>{@link Path#getFileSystem()
 * file system}</em> of the path argument is the same as the file system of the
 * current path. This is unlike the basic constraints of JSR 203 which only
 * require that the <em>{@link FileSystemProvider provider}</em> be the same.
 * </p>
 */
public abstract class AbstractPath
    implements PathBase
{
    @SuppressWarnings("ObjectEquality")
    @Override
    public boolean startsWith(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doStartsWith(other);
    }

    protected abstract boolean doStartsWith(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public boolean endsWith(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doEndsWith(other);
    }

    protected abstract boolean doEndsWith(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public Path resolve(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doResolve(other);
    }

    protected abstract Path doResolve(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public Path relativize(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doRelativize(other);
    }

    protected abstract Path doRelativize(Path other);
}
