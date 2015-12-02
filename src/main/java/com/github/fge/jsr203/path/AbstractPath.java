package com.github.fge.jsr203.path;

import com.github.fge.jsr203.FileSystemMismatchException;
import com.github.fge.jsr203.fs.AbstractFileSystem;

import java.nio.file.FileSystem;
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
 *
 * <p>Finally, it assumes by default that a path is {@link Path#isAbsolute()
 * absolute} if and only if it has a {@link Path#getRoot() root}, and vice
 * versa; this behaviour is overridable but in most cases you won't override it.
 * </p>
 */
public abstract class AbstractPath
    implements PathBase
{
    protected final AbstractFileSystem fs;

    protected AbstractPath(final AbstractFileSystem fs)
    {
        this.fs = fs;
    }

    @Override
    public final FileSystem getFileSystem()
    {
        return fs;
    }

    @Override
    public boolean isAbsolute()
    {
        return getRoot() != null;
    }

    @SuppressWarnings("ObjectEquality")
    @Override
    public final boolean startsWith(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doStartsWith(other);
    }

    protected abstract boolean doStartsWith(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public final boolean endsWith(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doEndsWith(other);
    }

    protected abstract boolean doEndsWith(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public final Path resolve(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doResolve(other);
    }

    protected abstract Path doResolve(Path other);

    @SuppressWarnings("ObjectEquality")
    @Override
    public final Path relativize(final Path other)
    {
        if (getFileSystem() != other.getFileSystem())
            throw new FileSystemMismatchException();

        return doRelativize(other);
    }

    protected abstract Path doRelativize(Path other);
}
