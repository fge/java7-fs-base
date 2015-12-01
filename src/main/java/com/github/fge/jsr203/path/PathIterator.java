package com.github.fge.jsr203.path;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class PathIterator
    implements Iterator<Path>
{
    private final PathBase path;
    private final int nameCount;
    @SuppressWarnings("RedundantFieldInitialization")
    private int index = 0;

    PathIterator(final PathBase path)
    {
        this.path = path;
        nameCount = path.getNameCount();
    }

    @Override
    public boolean hasNext()
    {
        return index < nameCount;
    }

    @Override
    public Path next()
    {
        if (hasNext())
            return path.getName(index++);

        throw new NoSuchElementException();

    }
}
