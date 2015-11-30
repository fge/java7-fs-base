package com.github.fge.jsr203.attrs.basic;

import com.github.fge.jsr203.attrs.AttributeConstants;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public interface BasicFileAttributesBase
    extends BasicFileAttributes
{
    @Override
    default FileTime lastModifiedTime()
    {
        return AttributeConstants.EPOCH;
    }

    @Override
    default FileTime lastAccessTime()
    {
        return AttributeConstants.EPOCH;
    }


    @Override
    default FileTime creationTime()
    {
        return AttributeConstants.EPOCH;
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
