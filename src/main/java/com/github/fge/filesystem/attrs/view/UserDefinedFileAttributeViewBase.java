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

package com.github.fge.filesystem.attrs.view;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.attribute.UserDefinedFileAttributeView;

@SuppressWarnings("DesignForExtension")
public abstract class UserDefinedFileAttributeViewBase
    implements UserDefinedFileAttributeView
{
    /**
     * Returns the name of this attribute view. Attribute views of this type
     * have the name {@code "user"}.
     */
    @Override
    public String name()
    {
        return "user";
    }

    /**
     * Writes the value of a user-defined attribute from a buffer.
     * <p> This method writes the value of the attribute from a given buffer as
     * a sequence of bytes. The size of the value to transfer is {@code r},
     * where {@code r} is the number of bytes remaining in the buffer, that is
     * {@code src.remaining()}. The sequence of bytes is transferred from the
     * buffer starting at index {@code p}, where {@code p} is the buffer's
     * position. Upon return, the buffer's position will be equal to {@code
     * p + n}, where {@code n} is the number of bytes transferred; its limit
     * will not have changed.
     * <p> If an attribute of the given name already exists then its value is
     * replaced. If the attribute does not exist then it is created. If it
     * implementation specific if a test to check for the existence of the
     * attribute and the creation of attribute are atomic with repect to other
     * file system activities.
     * <p> Where there is insufficient space to store the attribute, or the
     * attribute name or value exceed an implementation specific maximum size
     * then an {@code IOException} is thrown.
     * <p> <b>Usage Example:</b>
     * Suppose we want to write a file's MIME type as a user-defined attribute:
     * <pre>
     *    UserDefinedFileAttributeView view =
     *        FIles.getFileAttributeView(path, UserDefinedFileAttributeView
     *        .class);
     *    view.write("user.mimetype", Charset.defaultCharset().encode
     *    ("text/html"));
     * </pre>
     *
     * @param name The attribute name
     * @param src The buffer containing the attribute value
     * @return The number of bytes written, possibly zero
     *
     * @throws IOException If an I/O error occurs
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, and it denies {@link
     * RuntimePermission}<tt>("accessUserDefinedAttributes")</tt>
     * or its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file.
     */
    @Override
    public int write(final String name, final ByteBuffer src)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    /**
     * Deletes a user-defined attribute.
     *
     * @param name The attribute name
     * @throws IOException If an I/O error occurs or the attribute does not
     * exist
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, and it denies {@link
     * RuntimePermission}<tt>("accessUserDefinedAttributes")</tt>
     * or its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file.
     */
    @Override
    public void delete(final String name)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }
}
