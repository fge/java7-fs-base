package com.github.fge.jsr203.attrs.basic;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public final class FailedBasicFileAttributeView
    extends FailedFileAttributeView
    implements BasicFileAttributeViewBase
{
    public FailedBasicFileAttributeView(final IOException exception)
    {
        super(exception);
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw exception;
    }

    @Override
    public BasicFileAttributes readAttributes()
        throws IOException
    {
        throw exception;
    }
}
