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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

/**
 * An {@link IOException} defined when a file attribute is read only
 *
 * <p>Methods to set attributes in java.nio.file (such as {@link
 * Files#setAttribute(Path, String, Object, LinkOption...)}) only define {@link
 * IOException} as a possible exception; they do not seem to account for the
 * scenario when a file attribute is read only.</p>
 *
 * <p>Use this exception in your custom attribute views if you want to be able
 * to convey information about a read only attribute to user code.</p>
 */
public final class ReadOnlyAttributeException
    extends IOException
{
    /**
     * Constructs an {@code IOException} with {@code null}
     * as its error detail message.
     */
    public ReadOnlyAttributeException()
    {
    }

    /**
     * Constructs an {@code IOException} with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method)
     */
    public ReadOnlyAttributeException(final String message)
    {
        super(message);
    }

    /**
     * Constructs an {@code IOException} with the specified detail message
     * and cause.
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method)
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method).  (A null value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public ReadOnlyAttributeException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs an {@code IOException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause The cause (which is saved for later retrieval by the
     * {@link #getCause()} method).  (A null value is permitted,
     * and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public ReadOnlyAttributeException(final Throwable cause)
    {
        super(cause);
    }
}
