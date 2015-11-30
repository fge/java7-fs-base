package com.github.fge.jsr203.attrs.basic;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public interface BasicFileAttributesBase
    extends BasicFileAttributes
{
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
