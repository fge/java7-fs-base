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

package com.github.fge.filesystem.attributes.view;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;

@SuppressWarnings("DesignForExtension")
public abstract class DosFileAttributeViewBase
    extends BasicFileAttributeViewBase
    implements DosFileAttributeView
{
    /**
     * Returns the name of the attribute view. Attribute views of this type
     * have the name {@code "dos"}.
     */
    @Override
    public String name()
    {
        return "dos";
    }

    /**
     * Updates the value of the read-only attribute.
     * <p> It is implementation specific if the attribute can be updated as an
     * atomic operation with respect to other file system operations. An
     * implementation may, for example, require to read the existing value of
     * the DOS attribute in order to update this attribute.
     *
     * @param value the new value of the attribute
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default, and a security
     * manager is installed,
     * its  {@link SecurityManager#checkWrite(String) checkWrite} method
     * is invoked to check write access to the file
     */
    @Override
    public void setReadOnly(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    /**
     * Updates the value of the hidden attribute.
     * <p> It is implementation specific if the attribute can be updated as an
     * atomic operation with respect to other file system operations. An
     * implementation may, for example, require to read the existing value of
     * the DOS attribute in order to update this attribute.
     *
     * @param value the new value of the attribute
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default, and a security
     * manager is installed,
     * its  {@link SecurityManager#checkWrite(String) checkWrite} method
     * is invoked to check write access to the file
     */
    @Override
    public void setHidden(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();

    }

    /**
     * Updates the value of the system attribute.
     * <p> It is implementation specific if the attribute can be updated as an
     * atomic operation with respect to other file system operations. An
     * implementation may, for example, require to read the existing value of
     * the DOS attribute in order to update this attribute.
     *
     * @param value the new value of the attribute
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default, and a security
     * manager is installed,
     * its  {@link SecurityManager#checkWrite(String) checkWrite} method
     * is invoked to check write access to the file
     */
    @Override
    public void setSystem(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();

    }

    /**
     * Updates the value of the archive attribute.
     * <p> It is implementation specific if the attribute can be updated as an
     * atomic operation with respect to other file system operations. An
     * implementation may, for example, require to read the existing value of
     * the DOS attribute in order to update this attribute.
     *
     * @param value the new value of the attribute
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default, and a security
     * manager is installed,
     * its  {@link SecurityManager#checkWrite(String) checkWrite} method
     * is invoked to check write access to the file
     */
    @Override
    public void setArchive(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();

    }
}
