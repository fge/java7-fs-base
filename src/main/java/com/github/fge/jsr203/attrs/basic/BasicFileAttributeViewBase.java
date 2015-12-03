package com.github.fge.jsr203.attrs.basic;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

/**
 * Extension of {@link BasicFileAttributeView} with default implementations
 *
 * <p>The default implementations are as follows:</p>
 *
 * <ul>
 *     <li>{@link #name()} returns {@link StandardAttributeViewNames#BASIC};
 *     </li>
 *     <li>all methods setting attributes throw an {@link
 *     UnsupportedOperationException}.</li>
 * </ul>
 */
@FunctionalInterface
public interface BasicFileAttributeViewBase
    extends BasicFileAttributeView
{
    @Override
    default String name()
    {
        return StandardAttributeViewNames.BASIC;
    }

    @Override
    default void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
