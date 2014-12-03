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

package com.github.fge.filesystem.filestore;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Objects;

/**
 * Base implementation of a {@link FileStore}
 *
 * <p>Limitations:</p>
 *
 * <ul>
 *     <li>no support for {@link FileStoreAttributeView}s</li>
 * </ul>
 */
@ParametersAreNonnullByDefault
public abstract class FileStoreBase
    extends FileStore
{
    private final String name;
    private final String type;
    private final boolean readOnly;

    protected FileStoreBase(final String name, final String type,
        final boolean readOnly)
    {
        this.readOnly = readOnly;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    protected FileStoreBase(final String name, final boolean readOnly)
    {
        this(name, name, readOnly);
    }

    /**
     * Returns the name of this file store. The format of the name is highly
     * implementation specific. It will typically be the name of the storage
     * pool or volume.
     * <p> The string returned by this method may differ from the string
     * returned by the {@link Object#toString() toString} method.
     *
     * @return the name of this file store
     */
    @Override
    public final String name()
    {
        return name;
    }

    /**
     * Returns the <em>type</em> of this file store. The format of the string
     * returned by this method is highly implementation specific. It may
     * indicate, for example, the format used or if the file store is local
     * or remote.
     *
     * @return a string representing the type of this file store
     */
    @Override
    public final String type()
    {
        return type;
    }

    /**
     * Tells whether this file store is read-only. A file store is read-only if
     * it does not support write operations or other changes to files. Any
     * attempt to create a file, open an existing file for writing etc. causes
     * an {@code IOException} to be thrown.
     *
     * @return {@code true} if, and only if, this file store is read-only
     */
    @Override
    public final boolean isReadOnly()
    {
        return readOnly;
    }

    /**
     * Returns a {@code FileStoreAttributeView} of the given type.
     * <p> This method is intended to be used where the file store attribute
     * view defines type-safe methods to read or update the file store
     * attributes.
     * The {@code type} parameter is the type of the attribute view required and
     * the method returns an instance of that type if supported.
     *
     * @param type the {@code Class} object corresponding to the attribute view
     * @return a file store attribute view of the specified type or
     * {@code null} if the attribute view is not available
     */
    @Override
    public final <V extends FileStoreAttributeView> V getFileStoreAttributeView(
        final Class<V> type)
    {
        return null;
    }

    /**
     * Reads the value of a file store attribute.
     * <p> The {@code attribute} parameter identifies the attribute to be read
     * and takes the form:
     * <blockquote>
     * <i>view-name</i><b>:</b><i>attribute-name</i>
     * </blockquote>
     * where the character {@code ':'} stands for itself.
     * <p> <i>view-name</i> is the {@link FileStoreAttributeView#name name} of
     * a {@link FileStore AttributeView} that identifies a set of file
     * attributes.
     * <i>attribute-name</i> is the name of the attribute.
     * <p> <b>Usage Example:</b>
     * Suppose we want to know if ZFS compression is enabled (assuming the "zfs"
     * view is supported):
     * <pre>
     *    boolean compression = (Boolean)fs.getAttribute("zfs:compression");
     * </pre>
     *
     * @param attribute the attribute to read
     * @return the attribute value; {@code null} may be a valid valid for some
     * attributes
     *
     * @throws UnsupportedOperationException if the attribute view is not
     * available or it does not support
     * reading the attribute
     * @throws IOException if an I/O error occurs
     */
    @Override
    public final Object getAttribute(final String attribute)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
