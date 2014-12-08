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

package com.github.fge.filesystem.attributes.base;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

@SuppressWarnings("DesignForExtension")
public abstract class BasicFileAttributeViewBase
    implements BasicFileAttributeView
{
    /**
     * Returns the name of the attribute view. Attribute views of this type
     * have the name {@code "basic"}.
     */
    @Override
    public String name()
    {
        return "basic";
    }

    /**
     * Updates any or all of the file's last modified time, last access time,
     * and create time attributes.
     * <p> This method updates the file's timestamp attributes. The values are
     * converted to the epoch and precision supported by the file system.
     * Converting from finer to coarser granularities result in precision loss.
     * The behavior of this method when attempting to set a timestamp that is
     * not supported or to a value that is outside the range supported by the
     * underlying file store is not defined. It may or not fail by throwing an
     * {@code IOException}.
     * <p> If any of the {@code lastModifiedTime}, {@code lastAccessTime},
     * or {@code createTime} parameters has the value {@code null} then the
     * corresponding timestamp is not changed. An implementation may require to
     * read the existing values of the file attributes when only some, but not
     * all, of the timestamp attributes are updated. Consequently, this method
     * may not be an atomic operation with respect to other file system
     * operations. Reading and re-writing existing values may also result in
     * precision loss. If all of the {@code lastModifiedTime}, {@code
     * lastAccessTime} and {@code createTime} parameters are {@code null} then
     * this method has no effect.
     * <p> <b>Usage Example:</b>
     * Suppose we want to change a file's creation time.
     * <pre>
     *    Path path = ...
     *    FileTime time = ...
     *    Files.getFileAttributeView(path, BasicFileAttributeView.class)
     *    .setTimes(null, null, time);
     * </pre>
     *
     * @param lastModifiedTime the new last modified time, or {@code null} to
     * not change the
     * value
     * @param lastAccessTime the last access time, or {@code null} to not
     * change the value
     * @param createTime the file's create time, or {@code null} to not
     * change the value
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, its  {@link SecurityManager#checkWrite(String) checkWrite}
     * method is invoked to check write access to the file
     * @see Files#setLastModifiedTime
     */
    @Override
    public void setTimes(final FileTime lastModifiedTime,
        final FileTime lastAccessTime, final FileTime createTime)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }
}
