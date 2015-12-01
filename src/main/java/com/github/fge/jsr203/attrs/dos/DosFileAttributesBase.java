package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.basic.BasicFileAttributesBase;

import java.nio.file.attribute.DosFileAttributes;

/**
 * Extension of {@link DosFileAttributes} with default implementations
 *
 * <p>In addition to all default implementations in {@link
 * BasicFileAttributesBase} which this interface extends, it defines all DOS
 * specifc attribute methods as returning false.</p>
 */
public interface DosFileAttributesBase
    extends DosFileAttributes, BasicFileAttributesBase
{
    @Override
    default boolean isReadOnly()
    {
        return false;
    }

    @Override
    default boolean isHidden()
    {
        return false;
    }

    @Override
    default boolean isArchive()
    {
        return false;
    }

    @Override
    default boolean isSystem()
    {
        return false;
    }
}
