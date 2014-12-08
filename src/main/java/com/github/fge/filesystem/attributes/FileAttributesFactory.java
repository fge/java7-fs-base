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

package com.github.fge.filesystem.attributes;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@ParametersAreNonnullByDefault
public class FileAttributesFactory
{
    private static final MethodHandles.Lookup LOOKUP
        = MethodHandles.publicLookup();

    private final Map<String, Class<? extends FileAttributeView>> views
        = new HashMap<>();
    private final Map<String, Set<String>> viewEquivalences = new HashMap<>();

    private final Map<Class<? extends FileAttributeView>, Class<? extends FileAttributeView>>
        viewImpls = new HashMap<>();
    private final Map<Class<? extends FileAttributeView>, MethodHandle>
        constructors = new HashMap<>();

    private final Map<Class<? extends FileAttributeView>, Class<?>> attributeMap
        = new HashMap<>();
    private final Map<Class<? extends FileAttributeView>, MethodHandle>
        attributeHandles = new HashMap<>();

    public FileAttributesFactory()
    {
        /* acl */
        addView("acl", AclFileAttributeView.class);
        addViewEquivalence("acl", "owner");

        /* basic */
        addViewWithAttributes("basic", BasicFileAttributeView.class,
            BasicFileAttributes.class);

        /* dos */
        addViewWithAttributes("dos", DosFileAttributeView.class,
            DosFileAttributes.class);
        addViewEquivalence("dos", "basic");

        /* owner */
        addView("owner", FileOwnerAttributeView.class);

        /* posix */
        addViewWithAttributes("posix", PosixFileAttributeView.class,
            PosixFileAttributes.class);
        addViewEquivalence("posix", "basic", "owner");

        /* user */
        addView("user", UserDefinedFileAttributeView.class);
    }

    public final void addView(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        if (views.containsKey(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("view " + name
                + " already has an associated class");
        Objects.requireNonNull(viewClass);
        views.put(name, viewClass);
    }

    public final void addViewWithAttributes(final String name,
        final Class<? extends FileAttributeView> viewClass,
        final Class<?> attributeClass)
    {
        if (views.containsKey(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("view " + name
                + " already has an associated class");

        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(attributeClass);

        views.put(name, viewClass);
        attributeMap.put(viewClass, attributeClass);
    }

    public final void addViewEquivalence(final String name,
        final String... equivalences)
    {
        if (viewEquivalences.containsKey(Objects.requireNonNull(name)))
            throw new IllegalArgumentException("equivalences already defined"
                + " for " + name);
        if (equivalences.length == 0)
            throw new IllegalArgumentException("must define at least one "
                + "equivalence");

        final Set<String> set = new HashSet<>();
        for (final String equivalence : equivalences)
            set.add(Objects.requireNonNull(equivalence));

        viewEquivalences.put(name, Collections.unmodifiableSet(set));
    }

    public final void addImplementation(
        final Class<? extends FileAttributeView> viewClass,
        final Class<? extends FileAttributeView> implClass,
        final Class<?>... args)
    {
        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(implClass);

        final List<Class<?>> argList = new ArrayList<>();
        for (final Class<?> arg: args)
            argList.add(Objects.requireNonNull(arg));

        viewImpls.put(viewClass, implClass);

        MethodType type;
        MethodHandle handle;

        type = MethodType.methodType(void.class, argList);

        try {
            handle = LOOKUP.findConstructor(implClass, type);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("cannot find constructor", e);
        }

        type = handle.type().changeReturnType(viewClass);
        constructors.put(viewClass, handle.asType(type));

        final Class<?> attrClass = attributeMap.get(viewClass);

        if (attrClass == null)
            return;

        type = MethodType.methodType(attrClass);

        try {
            handle = LOOKUP.findVirtual(implClass, "readAttributes", type);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("cannot find readAttributes", e);
        }

        attributeHandles.put(implClass, handle);
    }
}
