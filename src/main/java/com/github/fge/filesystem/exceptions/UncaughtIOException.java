/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.filesystem.exceptions;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

/**
 * TODO! Due to a design fault, see description
 *
 * <p>The problem here is that for the moment, attribute view loading is
 * <strong>eager</strong> whereas it should be lazy.</p>
 *
 * @see FileSystemProvider#getFileAttributeView(Path, Class, LinkOption...)
 */
@SuppressWarnings("UncheckedExceptionClass")
public final class UncaughtIOException
    extends RuntimeException
{
    public UncaughtIOException()
    {
    }

    public UncaughtIOException(final String message)
    {
        super(message);
    }

    public UncaughtIOException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public UncaughtIOException(final Throwable cause)
    {
        super(cause);
    }

    public UncaughtIOException(final String message, final Throwable cause,
        final boolean enableSuppression, final boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
