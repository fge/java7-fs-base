package com.github.fge.jsr203.attrs.user;

import com.github.fge.jsr203.attrs.StandardAttributeViewNames;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * Extension of {@link UserDefinedFileAttributeView} with default
 * implementations
 *
 * <p>The default implementations are as follows:</p>
 *
 * <ul>
 *     <li>{@link #name()} returns {@link StandardAttributeViewNames#USER};</li>
 *     <li>all methods adding/removing/changing attributes throw an {@link
 *     UnsupportedOperationException}.</li>
 * </ul>
 */
public interface UserDefinedFileAttributeViewBase
    extends UserDefinedFileAttributeView
{
    @Override
    default String name()
    {
        return StandardAttributeViewNames.USER;
    }

    @Override
    default int write(final String name, final ByteBuffer src)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void delete(final String name)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
