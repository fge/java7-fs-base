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

import com.github.fge.filesystem.attributes.FileAttributesFactory;
import com.github.fge.filesystem.attributes.descriptor.AttributesDescriptor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Collection;
import java.util.Objects;

/**
 * Base implementation of a {@link FileStore}
 *
 * <p>Notes:</p>
 *
 * <ul>
 *     <li>at the moment, there is no support for {@link
 *     FileStoreAttributeView}s;</li>
 *     <li>by default, all size methods return {@link Long#MAX_VALUE}; as
 *     discussed on nio-dev, this is a reasonable value to return when the file
 *     store has no information on the actual size.</li>
 * </ul>
 *
 * @see FileStore
 */
@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class FileStoreBase
    extends FileStore
{
    private final String name;
    private final String type;
    private final boolean readOnly;
    private final FileAttributesFactory factory;

    /**
     * Main constructor
     *
     * @param name the name of the file store
     * @param type the type of the file store
     * @param factory the associated {@link FileAttributesFactory}
     * @param readOnly whether this filestore is read only
     */
    protected FileStoreBase(final String name, final String type,
        final FileAttributesFactory factory, final boolean readOnly)
    {
        this.readOnly = readOnly;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Alternate constructor
     *
     * <p>This constructor assumes that the name and type are the same; apart
     * from that, all arguments are the same as for the other constructor.</p>
     *
     * @param name the name (and type) of the file store
     * @param factory the associated {@link FileAttributesFactory}
     * @param readOnly whether this filestore is read only
     */
    protected FileStoreBase(final String name,
        final FileAttributesFactory factory, final boolean readOnly)
    {
        this(name, name, factory, readOnly);
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
    public long getTotalSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUsableSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public long getUnallocatedSpace()
        throws IOException
    {
        return Long.MAX_VALUE;
    }

    @Override
    public final boolean supportsFileAttributeView(
        final Class<? extends FileAttributeView> type)
    {
        Objects.requireNonNull(type);

        final Collection<AttributesDescriptor> descriptors
            = factory.getDescriptors().values();

        for (final AttributesDescriptor descriptor: descriptors)
            if (type.isAssignableFrom(descriptor.getViewClass()))
                return true;

        return false;
    }

    @Override
    public final boolean supportsFileAttributeView(final String name)
    {
        Objects.requireNonNull(name);
        // TODO: not good; should build this list when registering providers
        return factory.getDescriptors().containsKey(name);
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
