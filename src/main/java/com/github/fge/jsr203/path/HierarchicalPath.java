package com.github.fge.jsr203.path;

import java.nio.file.Path;

/**
 * An extension over {@link PathBase} for hierarchical filesystems
 *
 * <p>Hierarchical filesystems are the most common case; and in this case, a
 * path is absolute if and only if the root is null.</p>
 *
 * @see Path#getRoot()
 */
public interface HierarchicalPath
    extends PathBase
{
    @Override
    default boolean isAbsolute()
    {
        return getRoot() != null;
    }
}
