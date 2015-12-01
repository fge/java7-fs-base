package com.github.fge.jsr203.attrs.basic;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * Extension of {@link BasicFileAttributes} with default implementations
 *
 * <p>The default implementations are as follows:</p>
 *
 * <ul>
 *     <li>all methods returning a {@link FileTime} return {@link
 *     BasicFileAttributesBase#EPOCH};</li>
 *     <li>{@link #isSymbolicLink()} and {@link #isOther()} return false;</li>
 *     <li>{@link #fileKey()} returns null.</li>
 * </ul>
 */
public interface BasicFileAttributesBase
    extends BasicFileAttributes
{
    /**
     * Jan 1st, 1970, 00:00:00, GMT as a {@link FileTime}
     */
    @SuppressWarnings("ConstantDeclaredInInterface")
    FileTime EPOCH = FileTime.fromMillis(0L);

    @Override
    default FileTime lastModifiedTime()
    {
        return EPOCH;
    }

    @Override
    default FileTime lastAccessTime()
    {
        return EPOCH;
    }


    @Override
    default FileTime creationTime()
    {
        return EPOCH;
    }

    @Override
    default boolean isSymbolicLink()
    {
        return false;
    }

    @Override
    default boolean isOther()
    {
        return false;
    }

    @Override
    default Object fileKey()
    {
        return null;
    }
}
