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

import java.nio.file.spi.FileSystemProvider;

/**
 * Exception thrown when an attribute does not exist for a given attribute view
 *
 * <p>This exception is a more precise version of (and extends) {@link
 * IllegalArgumentException} defined to be thrown by {@link FileSystemProvider}
 * methods dealing with reading/writing attributes.</p>
 */
@SuppressWarnings("UncheckedExceptionClass")
public final class NoSuchAttributeException
    extends IllegalArgumentException
{
    public NoSuchAttributeException()
    {
    }

    public NoSuchAttributeException(final String s)
    {
        super(s);
    }

    public NoSuchAttributeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public NoSuchAttributeException(final Throwable cause)
    {
        super(cause);
    }
}
