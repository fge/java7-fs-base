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

import com.github.fge.filesystem.driver.FileSystemDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

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
 *
 * @see FileSystemDriver#setAttribute(Path, String, Object, LinkOption...)
 * @see FileSystemProvider#setAttribute(Path, String, Object, LinkOption...)
 */
public final class ReadOnlyAttributeException
    extends IOException
{
    public ReadOnlyAttributeException()
    {
    }

    public ReadOnlyAttributeException(final String message)
    {
        super(message);
    }

    public ReadOnlyAttributeException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public ReadOnlyAttributeException(final Throwable cause)
    {
        super(cause);
    }
}
