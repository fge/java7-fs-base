package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.attrs.basic.BasicFileAttributeViewBase;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;

/**
 * Extension of {@link DosFileAttributeView} with default implementations
 *
 * <p>The default implementations are as follows:</p>
 *
 * <ul>
 *     <li>{@link #name()} returns {@link StandardAttributeViewNames#DOS};</li>
 *     <li>all methods setting attributes throw an {@link
 *     UnsupportedOperationException} (this includes all methods inherited from
 *     {@link BasicFileAttributeViewBase}, which this interface extends).</li>
 * </ul>
 */
@FunctionalInterface
public interface DosFileAttributeViewBase
    extends DosFileAttributeView, BasicFileAttributeViewBase
{
    @Override
    default String name()
    {
        return StandardAttributeViewNames.DOS;
    }

    @Override
    default void setReadOnly(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setHidden(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setSystem(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    default void setArchive(final boolean value)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
