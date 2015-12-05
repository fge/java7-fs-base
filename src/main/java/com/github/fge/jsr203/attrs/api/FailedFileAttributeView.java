package com.github.fge.jsr203.attrs.api;

import java.io.IOException;

public abstract class FailedFileAttributeView
{
    protected final IOException exception;

    protected FailedFileAttributeView(final IOException exception)
    {
        this.exception = exception;
    }
}
