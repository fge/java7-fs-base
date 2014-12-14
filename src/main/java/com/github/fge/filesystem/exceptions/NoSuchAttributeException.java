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

import java.security.PrivilegedActionException;

public final class NoSuchAttributeException
    extends IllegalArgumentException
{
    /**
     * Constructs an <code>IllegalArgumentException</code> with no
     * detail message.
     */
    public NoSuchAttributeException()
    {
    }

    /**
     * Constructs an <code>IllegalArgumentException</code> with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public NoSuchAttributeException(final String s)
    {
        super(s);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     * <p>Note that the detail message associated with <code>cause</code> is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.
     *
     * @param message the detail message (which is saved for later retrieval
     * by the {@link Throwable#getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the
     * {@link Throwable#getCause()} method).  (A <tt>null</tt> value
     * is permitted, and indicates that the cause is nonexistent or
     * unknown.)
     * @since 1.5
     */
    public NoSuchAttributeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     * This constructor is useful for exceptions that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     *
     * @param cause the cause (which is saved for later retrieval by the
     * {@link Throwable#getCause()} method).  (A <tt>null</tt> value is
     * permitted, and indicates that the cause is nonexistent or
     * unknown.)
     * @since 1.5
     */
    public NoSuchAttributeException(final Throwable cause)
    {
        super(cause);
    }
}
