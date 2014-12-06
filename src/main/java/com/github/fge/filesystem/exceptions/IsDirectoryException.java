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
import java.nio.file.FileSystemException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

/**
 * Exception defined when the source or target of a filesystem I/O operation
 * is a directory and should not be
 *
 * <p>Strangely enough, this exception is not defined by the JDK; this package
 * defines it as inheriting {@link FileSystemException} (which itself inherits
 * {@link IOException}) to account for situations where an operation is
 * performed on a path which is a directory where it should not be; the primary
 * candidates for such an exception are an attempt to open an {@link
 * FileSystemDriver#newInputStream(Path, OpenOption...) input stream} or an
 * {@link FileSystemDriver#newOutputStream(Path, OpenOption...) output stream}
 * on a path which is a directory: such methods make no sense if the path is
 * considered to be a directory by the filesystem.</p>
 *
 * @see FileSystemDriver#newInputStream(Path, OpenOption...)
 * @see FileSystemDriver#newOutputStream(Path, OpenOption...)
 * @see FileSystemProvider#newInputStream(Path, OpenOption...)
 * @see FileSystemProvider#newOutputStream(Path, OpenOption...)
 */


@SuppressWarnings("UnusedDeclaration")
public final class IsDirectoryException
    extends FileSystemException
{
    /**
     * Constructs an instance of this class. This constructor should be used
     * when an operation involving one file fails and there isn't any additional
     * information to explain the reason.
     *
     * @param file a string identifying the file or {@code null} if not known.
     */
    public IsDirectoryException(final String file)
    {
        super(file);
    }

    /**
     * Constructs an instance of this class. This constructor should be used
     * when an operation involving two files fails, or there is additional
     * information to explain the reason.
     *
     * @param file a string identifying the file or {@code null} if not known.
     * @param other a string identifying the other file or {@code null} if there
     * isn't another file or if not known
     * @param reason a reason message with additional information or {@code
     * null}
     */
    public IsDirectoryException(final String file, final String other,
        final String reason)
    {
        super(file, other, reason);
    }
}
