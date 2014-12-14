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

import com.github.fge.filesystem.attributes.FileAttributesFactory;
import com.github.fge.filesystem.attributes.provider.FileAttributesProvider;

/**
 * Exception thrown when an invalid {@link FileAttributesProvider} is found
 *
 * @see FileAttributesFactory
 */
@SuppressWarnings("UncheckedExceptionClass")
public final class InvalidAttributeProviderException
    extends IllegalArgumentException
{
    public InvalidAttributeProviderException()
    {
    }

    public InvalidAttributeProviderException(final String s)
    {
        super(s);
    }

    public InvalidAttributeProviderException(final String message,
        final Throwable cause)
    {
        super(message, cause);
    }

    public InvalidAttributeProviderException(final Throwable cause)
    {
        super(cause);
    }
}
