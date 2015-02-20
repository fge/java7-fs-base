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

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class MetadataFactoryBuilder
{
    private static final String VIEW_ALREADY_DEFINED
        = "attribute view \"%s\" is already defined";
    private static final String ILLEGAL_VIEW_NAME
        = "illegal view name \"%s\"";

    private static final Map<String, Class<?>> PROVIDED_VIEWS;

    private static final Map<Class<?>, Class<?>> PROVIDED_ATTRIBUTES;

    static {
        final Map<String, Class<?>> map = new HashMap<>();

        /*
         * First the views
         */
        map.put("acl", AclFileAttributeView.class);
        map.put("basic", BasicFileAttributeView.class);
        map.put("dos", DosFileAttributeView.class);
        map.put("owner", FileOwnerAttributeView.class);
        map.put("posix", PosixFileAttributeView.class);
        map.put("user", UserDefinedFileAttributeView.class);

        PROVIDED_VIEWS = new HashMap<>(map);

        final Map<Class<?>, Class<?>> classMap = new HashMap<>();

        classMap.put(BasicFileAttributeView.class, BasicFileAttributes.class);
        classMap.put(DosFileAttributeView.class, DosFileAttributes.class);
        classMap.put(PosixFileAttributeView.class, PosixFileAttributes.class);

        PROVIDED_ATTRIBUTES = Collections.unmodifiableMap(classMap);

    }

    final Map<String, Class<?>> views = new HashMap<>(PROVIDED_VIEWS);
    final Map<Class<?>, Class<?>> attributes
        = new HashMap<>(PROVIDED_ATTRIBUTES);

    public MetadataFactoryBuilder addDefinedView(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(viewClass);

        doAddDefinedView(name, viewClass);

        return this;
    }

    public MetadataFactoryBuilder addDefinedViewWithAttributes(
        final String name, final Class<? extends FileAttributeView> viewClass,
        final Class<? extends BasicFileAttributes> attributesClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(attributesClass);

        doAddDefinedView(name, viewClass);

        attributes.put(viewClass, attributesClass);

        return this;
    }

    @VisibleForTesting
    void doAddDefinedView(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        if (name.isEmpty() || name.indexOf(':') != -1)
            throw new IllegalArgumentException(
                String.format(ILLEGAL_VIEW_NAME, name));

        if (views.put(name, viewClass) != null)
            throw new IllegalArgumentException(
                String.format(VIEW_ALREADY_DEFINED, name));
    }
}
