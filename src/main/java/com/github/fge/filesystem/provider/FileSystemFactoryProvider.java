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

package com.github.fge.filesystem.provider;

import com.github.fge.filesystem.attributes.FileAttributesFactory;
import com.github.fge.filesystem.options.FileSystemOptionsFactory;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.UnixPathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class FileSystemFactoryProvider
{
    private static final PathElementsFactory PATH_ELEMENTS_FACTORY
        = new UnixPathElementsFactory();
    private static final PathMatcherFactory PATH_MATCHER_FACTORY
        = new PathMatcherFactory();
    private static final FileSystemOptionsFactory OPTIONS_FACTORY
        = new FileSystemOptionsFactory();

    private PathElementsFactory pathElementsFactory;
    private PathMatcherFactory pathMatcherFactory;
    private FileAttributesFactory attributesFactory;
    private FileSystemOptionsFactory optionsFactory;

    public FileSystemFactoryProvider()
    {
        setPathElementsFactory(PATH_ELEMENTS_FACTORY);
        setPathMatcherFactory(PATH_MATCHER_FACTORY);
        setOptionsFactory(OPTIONS_FACTORY);
    }

    @Nonnull
    public final PathElementsFactory getPathElementsFactory()
    {
        return pathElementsFactory;
    }

    @Nonnull
    public final PathMatcherFactory getPathMatcherFactory()
    {
        return pathMatcherFactory;
    }

    @Nonnull
    public final FileAttributesFactory getAttributesFactory()
    {
        return attributesFactory;
    }

    @Nonnull
    public final FileSystemOptionsFactory getOptionsFactory()
    {
        return optionsFactory;
    }

    public final void validate()
    {
        Objects.requireNonNull(attributesFactory,
            "attributes factory must not be null");
        if (!attributesFactory.supportsFileAttributeView("basic"))
            throw new IllegalArgumentException("\"basic\" file attribute view"
                + " must be supported");
    }

    protected final void setPathElementsFactory(
        final PathElementsFactory pathElementsFactory
    )
    {
        this.pathElementsFactory = Objects.requireNonNull(pathElementsFactory);
    }

    protected final void setPathMatcherFactory(
        final PathMatcherFactory pathMatcherFactory
    )
    {
        this.pathMatcherFactory = Objects.requireNonNull(pathMatcherFactory);
    }

    protected final void setAttributesFactory(
        final FileAttributesFactory attributesFactory
    )
    {
        this.attributesFactory = Objects.requireNonNull(attributesFactory);
    }

    protected final void setOptionsFactory(
        final FileSystemOptionsFactory optionsFactory
    )
    {
        this.optionsFactory = Objects.requireNonNull(optionsFactory);
    }
}
