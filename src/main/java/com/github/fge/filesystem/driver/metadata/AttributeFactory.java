/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.driver.metadata;

import com.github.fge.filesystem.internal.VisibleForTesting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AttributeFactory<D extends MetadataDriver<M>, M>
{
    static final String VIEW_CLASS_NOT_SUPPORTED
        = "view with class %s is not supported";
    @VisibleForTesting
    static final String ATTRS_NOT_SUPPORTED
        = "attributes with name %s are not supported";

    private final D driver;

    private final Map<String, Class<?>> definedViews;
    private final Map<String, Class<?>> definedAttributes;

    private final Map<Class<?>, MethodHandle> viewHandles;
    private final Map<Class<?>, MethodHandle> attributesHandles;

    private final Map<String, List<String>> viewAliases;
    private final Map<String, List<String>> attributesAliases;

    public static <D2 extends MetadataDriver<M2>, M2> AttributeFactoryBuilder<D2, M2> newBuilder(
        final Class<M2> metadataClass)
    {
        return new AttributeFactoryBuilder<>(metadataClass);
    }

    AttributeFactory(final AttributeFactoryBuilder<D, M> builder)
    {
        driver = builder.driver;

        definedViews = new HashMap<>(builder.definedViews);
        definedAttributes = new HashMap<>(builder.definedAttributes);

        viewHandles = new HashMap<>(builder.viewHandles);
        attributesHandles = new HashMap<>(builder.attributesHandles);

        viewAliases = new HashMap<>(builder.viewsAliases);
        attributesAliases = new HashMap<>(builder.attributesAliases);
    }

    @Nonnull
    public D getDriver()
    {
        return driver;
    }

    public <A extends BasicFileAttributes> A getAttributes(final String name,
        final M metadata)
        throws IOException
    {
        final Class<?> baseClass = definedAttributes.get(name);
        final String errmsg = String.format(ATTRS_NOT_SUPPORTED, name);

        if (baseClass == null)
            throw new UnsupportedOperationException(errmsg);

        final MethodHandle handle = findAttributesHandle(baseClass);

        if (handle == null)
            throw new UnsupportedOperationException(errmsg);

        try {
            //noinspection unchecked
            return (A) handle.invoke(metadata);
        } catch (IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @Nullable
    private MethodHandle findAttributesHandle(final Class<?> baseClass)
    {
        final MethodHandle handle = attributesHandles.get(baseClass);

        if (handle != null)
            return handle;

        for (final Map.Entry<Class<?>, MethodHandle> entry:
            attributesHandles.entrySet())
            if (baseClass.isAssignableFrom(entry.getKey()))
                return entry.getValue();

        return null;
    }

    public <V extends FileAttributeView> V getView(final Path path,
        final Class<V> viewClass)
    {
        final MethodHandle handle = findViewHandle(viewClass);

        if (handle == null)
            throw new UnsupportedOperationException(String.format(
                VIEW_CLASS_NOT_SUPPORTED, viewClass.getCanonicalName()
            ));

        try {
            return (V) handle.invoke(path, this);
        } catch (Throwable throwable) {
            // TODO!
            throw new RuntimeException(throwable);
        }
    }

    @Nullable
    private MethodHandle findViewHandle(final Class<?> viewClass)
    {
        final MethodHandle handle = viewHandles.get(viewClass);

        if (handle != null)
            return handle;

        for (final Map.Entry<Class<?>, MethodHandle> entry:
            viewHandles.entrySet())
            if (viewClass.isAssignableFrom(entry.getKey()))
                return entry.getValue();

        return null;
    }
}
