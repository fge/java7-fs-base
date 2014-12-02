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
import java.nio.file.FileStore;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class AbstractFileStore
    extends FileStore
{
    private final String name;
    private final String type;
    private final boolean readOnly;

    protected AbstractFileStore(final String name, final String type,
        final boolean readOnly)
    {
        this.readOnly = readOnly;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    protected AbstractFileStore(final String name, final boolean readOnly)
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
}
