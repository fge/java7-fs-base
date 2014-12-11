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

    @Override
    public final String name()
    {
        return name;
    }

    @Override
    public final String type()
    {
        return type;
    }

    @Override
    public final boolean isReadOnly()
    {
        return readOnly;
    }

    @Override
    public final <V extends FileStoreAttributeView> V getFileStoreAttributeView(
        final Class<V> type)
    {
        return null;
    }

    @Override
    public final Object getAttribute(final String attribute)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
