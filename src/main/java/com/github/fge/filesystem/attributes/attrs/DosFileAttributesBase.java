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

package com.github.fge.filesystem.attributes.attrs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;

/**
 * Basic abstract implementation of {@link DosFileAttributes}
 *
 * <p>All methods specifically defined by this attribute class return {@code
 * false} by default (overridable).</p>
 *
 * <p>Note that this class also gives access to all {@link BasicFileAttributes}.
 * </p>
 */
@SuppressWarnings("DesignForExtension")
public abstract class DosFileAttributesBase
    extends BasicFileAttributesBase
    implements DosFileAttributes
{
    /**
     * Returns the value of the system attribute.
     * <p> This attribute is often used to indicate that the file is a component
     * of the operating system.
     *
     * @return the value of the system attribute
     */
    @Override
    public boolean isSystem()
    {
        return false;
    }

    /**
     * Returns the value of the archive attribute.
     * <p> This attribute is typically used by backup programs.
     *
     * @return the value of the archive attribute
     */
    @Override
    public boolean isArchive()
    {
        return false;
    }

    /**
     * Returns the value of the hidden attribute.
     * <p> This attribute is often used to indicate if the file is visible to
     * users.
     *
     * @return the value of the hidden attribute
     */
    @Override
    public boolean isHidden()
    {
        return false;
    }

    /**
     * Returns the value of the read-only attribute.
     * <p> This attribute is often used as a simple access control mechanism
     * to prevent files from being deleted or updated. Whether the file system
     * or platform does any enforcement to prevent <em>read-only</em> files
     * from being updated is implementation specific.
     *
     * @return the value of the read-only attribute
     */
    @Override
    public boolean isReadOnly()
    {
        return false;
    }
}
