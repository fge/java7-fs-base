package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.api.FailedFileAttributeView;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;

public final class FailedDosFileAttributeView
    extends FailedFileAttributeView
    implements DosFileAttributeViewBase
{
    public FailedDosFileAttributeView(final IOException exception)
    {
        super(exception);
    }

    @Override
    public DosFileAttributes readAttributes()
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setReadOnly(final boolean value)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setHidden(final boolean value)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setSystem(final boolean value)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setArchive(final boolean value)
        throws IOException
    {
        throw exception;
    }

    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw exception;
    }
}
