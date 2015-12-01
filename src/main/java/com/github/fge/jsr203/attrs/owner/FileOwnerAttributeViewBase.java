package com.github.fge.jsr203.attrs.owner;

import com.github.fge.jsr203.attrs.StandardAttributeViewNames;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

/**
 * Extension of {@link FileOwnerAttributeView} with default implementations
 *
 * <p>The default implementations as as follows:</p>
 *
 * <ul>
 *     <li>{@link #name()} returns {@link StandardAttributeViewNames#OWNER};
 *     </li>
 *     <li>all methods setting attributes throw an {@link
 *     UnsupportedOperationException}.</li>
 * </ul>
 */
@FunctionalInterface
public interface FileOwnerAttributeViewBase
    extends FileOwnerAttributeView
{
    @Override
    default String name()
    {
        return StandardAttributeViewNames.OWNER;
    }

    @Override
    default void setOwner(final UserPrincipal owner)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
