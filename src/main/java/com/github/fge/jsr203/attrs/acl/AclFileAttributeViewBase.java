package com.github.fge.jsr203.attrs.acl;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.attrs.owner.FileOwnerAttributeViewBase;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.util.List;

/**
 * Extension of {@link AclFileAttributeView} with default implementations
 *
 * <p>The default implementations are as follows:</p>
 *
 * <ul>
 *     <li>{@link #name()} returns {@link StandardAttributeViewNames#ACL};</li>
 *     <li>all methods setting attributes throw an {@link
 *     UnsupportedOperationException} (this includes methods defined in {@link
 *     FileOwnerAttributeViewBase} which this interface extends).</li>
 * </ul>
 */
public interface AclFileAttributeViewBase
    extends AclFileAttributeView, FileOwnerAttributeViewBase
{
    @Override
    default String name()
    {
        return StandardAttributeViewNames.ACL;
    }

    @Override
    default void setAcl(final List<AclEntry> acl)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
