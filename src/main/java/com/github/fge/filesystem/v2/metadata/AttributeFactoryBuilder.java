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

package com.github.fge.filesystem.v2.metadata;

import com.github.fge.filesystem.v2.metadata.read.AclViewReader;
import com.github.fge.filesystem.v2.metadata.read.BasicViewReader;
import com.github.fge.filesystem.v2.metadata.read.DosViewReader;
import com.github.fge.filesystem.v2.metadata.read.FileOwnerViewReader;
import com.github.fge.filesystem.v2.metadata.read.PosixViewReader;
import com.github.fge.filesystem.v2.metadata.read.UserDefinedViewReader;
import com.github.fge.filesystem.v2.metadata.write.AclViewWriter;
import com.github.fge.filesystem.v2.metadata.write.BasicViewWriter;
import com.github.fge.filesystem.v2.metadata.write.DosViewWriter;
import com.github.fge.filesystem.v2.metadata.write.FileOwnerViewWriter;
import com.github.fge.filesystem.v2.metadata.write.PosixViewWriter;
import com.github.fge.filesystem.v2.metadata.write.UserDefinedViewWriter;
import com.github.fge.filesystem.internal.VisibleForTesting;
import com.github.fge.filesystem.v2.driver.MetadataDriver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Path;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AttributeFactoryBuilder<D extends MetadataDriver<M>, M>
{
    private static final MethodHandles.Lookup LOOKUP
        = MethodHandles.publicLookup();

    @VisibleForTesting
    static final String NO_SUCH_ATTRIBUTES = "no attributes with name %s";
    @VisibleForTesting
    static final String NO_SUCH_VIEW = "no attribute view with name %s";
    @VisibleForTesting
    static final String NOT_SUBCLASS = "class %s is not a subclass of %s";
    @VisibleForTesting
    static final String NO_SUCH_CONSTRUCTOR
        = "class %s has no constructor with signature %s";
    @VisibleForTesting
    static final String NO_DRIVER = "no metadata driver was provided";
    @VisibleForTesting
    static final String NO_BASIC_VIEW
        = "no implementation of BasicFileAttributeView was provided";
    @VisibleForTesting
    static final String NO_BASIC_ATTRS
        = "no implementation of BasicFileAttributes was provided";
    @VisibleForTesting
    static final String VIEW_ATTR_NOIMPL
        = "no attributes implementation for view %s";
    @VisibleForTesting
    static final String ATTR_VIEW_NOIMPL
        = "no view implementation for attributes %s";

    private static final Map<String, Class<?>> BUILTIN_VIEWS;
    private static final Map<String, Class<?>> BUILTIN_ATTRIBUTES;

    static {
        final Map<String, Class<?>> classMap = new HashMap<>();

        String name;
        Class<? extends FileAttributeView> viewClass;

        name = "acl";
        viewClass = AclFileAttributeView.class;
        classMap.put(name, viewClass);

        name = "basic";
        viewClass = BasicFileAttributeView.class;
        classMap.put(name, viewClass);

        name = "dos";
        viewClass = DosFileAttributeView.class;
        classMap.put(name, viewClass);

        name = "owner";
        viewClass = FileOwnerAttributeView.class;
        classMap.put(name, viewClass);

        name = "posix";
        viewClass = PosixFileAttributeView.class;
        classMap.put(name, viewClass);

        name = "user";
        viewClass = UserDefinedFileAttributeView.class;
        classMap.put(name, viewClass);

        BUILTIN_VIEWS = Collections.unmodifiableMap(new HashMap<>(classMap));

        classMap.clear();

        Class<? extends BasicFileAttributes> attributesClass;

        name = "basic";
        attributesClass = BasicFileAttributes.class;
        classMap.put(name, attributesClass);

        name = "dos";
        attributesClass = DosFileAttributes.class;
        classMap.put(name, attributesClass);

        name = "posix";
        attributesClass = PosixFileAttributes.class;
        classMap.put(name, attributesClass);

        BUILTIN_ATTRIBUTES = Collections.unmodifiableMap(new HashMap<>(classMap));
    }

    private static final Map<String, List<String>> VIEWS_ALIASES;
    private static final Map<String, List<String>> ATTRIBUTES_ALIASES;

    static {
        final Map<String, List<String>> map = new HashMap<>();

        String name;
        List<String> aliases;

        name = "acl";
        aliases = Collections.singletonList("owner");
        map.put(name, Collections.unmodifiableList(aliases));

        name = "dos";
        aliases = Collections.singletonList("basic");
        map.put(name, Collections.unmodifiableList(aliases));

        name = "posix";
        aliases = Arrays.asList("owner", "basic");
        map.put(name, Collections.unmodifiableList(aliases));

        VIEWS_ALIASES = Collections.unmodifiableMap(new HashMap<>(map));

        map.clear();

        name = "dos";
        aliases = Collections.singletonList("basic");
        map.put(name, Collections.unmodifiableList(aliases));

        name = "posix";
        aliases = Collections.singletonList("basic");
        map.put(name, Collections.unmodifiableList(aliases));

        ATTRIBUTES_ALIASES = Collections.unmodifiableMap(
            new HashMap<>(map));
    }

    private static final Map<Class<?>, MethodHandle> BUILTIN_READERS;
    private static final Map<Class<?>, MethodHandle> BUILTIN_WRITERS;

    static {
        final Map<Class<?>, MethodHandle> readerMap = new HashMap<>();
        final Map<Class<?>, MethodHandle> writerMap = new HashMap<>();

        Class<? extends FileAttributeView> view;
        MethodHandle reader, writer;

        view = AclFileAttributeView.class;
        reader = getReaderWriterHandle(AclViewReader.class, view);
        writer = getReaderWriterHandle(AclViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        view = BasicFileAttributeView.class;
        reader = getReaderWriterHandle(BasicViewReader.class, view);
        writer = getReaderWriterHandle(BasicViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        view = DosFileAttributeView.class;
        reader = getReaderWriterHandle(DosViewReader.class, view);
        writer = getReaderWriterHandle(DosViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        view = FileOwnerAttributeView.class;
        reader = getReaderWriterHandle(FileOwnerViewReader.class, view);
        writer = getReaderWriterHandle(FileOwnerViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        view = PosixFileAttributeView.class;
        reader = getReaderWriterHandle(PosixViewReader.class, view);
        writer = getReaderWriterHandle(PosixViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        view = UserDefinedFileAttributeView.class;
        reader = getReaderWriterHandle(UserDefinedViewReader.class, view);
        writer = getReaderWriterHandle(UserDefinedViewWriter.class, view);
        readerMap.put(view, reader);
        writerMap.put(view, writer);

        BUILTIN_READERS = Collections.unmodifiableMap(readerMap);
        BUILTIN_WRITERS = Collections.unmodifiableMap(writerMap);
    }

    final MethodType viewConstructor;
    final MethodType attributesConstructor;

    final Map<String, Class<?>> definedViews
        = new HashMap<>(BUILTIN_VIEWS);
    final Map<String, Class<?>> definedAttributes
        = new HashMap<>(BUILTIN_ATTRIBUTES);

    final Map<Class<?>, MethodHandle> readerHandles
        = new HashMap<>(BUILTIN_READERS);
    final Map<Class<?>, MethodHandle> writerHandles
        = new HashMap<>(BUILTIN_WRITERS);

    final Map<Class<?>, MethodHandle> viewHandles = new HashMap<>();
    final Map<Class<?>, MethodHandle> attributesHandles = new HashMap<>();

    final Map<String, List<String>> viewsAliases
        = new HashMap<>(VIEWS_ALIASES);
    final Map<String, List<String>> attributesAliases
        = new HashMap<>(ATTRIBUTES_ALIASES);

    D driver;

    AttributeFactoryBuilder(final Class<M> metadataClass)
    {
        viewConstructor = MethodType.methodType(void.class, Path.class,
            AttributeFactory.class);
        attributesConstructor = MethodType.methodType(void.class,
            metadataClass);
    }

    public AttributeFactoryBuilder<D, M> withDriver(final D driver)
    {
        this.driver = Objects.requireNonNull(driver);
        return this;
    }

    public <A extends BasicFileAttributes> AttributeFactoryBuilder<D, M>
    addAttributesImplementation(final String name,
        final Class<A> attributesClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(attributesClass);

        final Class<?> baseClass = definedAttributes.get(name);

        String errmsg;

        errmsg = String.format(NO_SUCH_ATTRIBUTES, name);

        if (baseClass == null)
            throw new IllegalArgumentException(errmsg);

        errmsg = String.format(NOT_SUBCLASS, attributesClass.getCanonicalName(),
            baseClass.getCanonicalName());

        if (!baseClass.isAssignableFrom(attributesClass))
            throw new IllegalArgumentException(errmsg);

        errmsg = String.format(NO_SUCH_CONSTRUCTOR,
            attributesClass.getCanonicalName(),
            attributesConstructor);

        final MethodHandle handle;

        try {
            handle = LOOKUP.findConstructor(attributesClass,
                attributesConstructor);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException(errmsg, e);
        }

        attributesHandles.put(baseClass, handle);

        return this;
    }

    public <V extends FileAttributeView> AttributeFactoryBuilder<D, M>
    addViewImplementation(final String name, final Class<V> viewClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(viewClass);

        final Class<?> baseClass = definedViews.get(name);

        String errmsg;

        errmsg = String.format(NO_SUCH_VIEW, name);

        if (baseClass == null)
            throw new IllegalArgumentException(errmsg);

        errmsg = String.format(NOT_SUBCLASS, viewClass.getCanonicalName(),
            baseClass.getCanonicalName());

        if (!baseClass.isAssignableFrom(viewClass))
            throw new IllegalArgumentException(errmsg);

        errmsg = String.format(NO_SUCH_CONSTRUCTOR,
            viewClass.getCanonicalName(), viewConstructor);

        final MethodHandle handle;

        try {
            handle = LOOKUP.findConstructor(viewClass, viewConstructor);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException(errmsg, e);
        }

        viewHandles.put(baseClass, handle);

        return this;
    }

    public AttributeFactory<D, M> build()
    {
        if (driver == null)
            throw new IllegalArgumentException(NO_DRIVER);

        checkForBasicView();
        checkForBasicAttributes();
        checkAttributesImplementations();
        checkViewImplementations();

        return new AttributeFactory<>(this);
    }

    private void checkForBasicView()
    {
        final Class<?> wanted = BasicFileAttributeView.class;

        if (viewHandles.containsKey(wanted))
            return;

        for (final Class<?> c: viewHandles.keySet())
            if (wanted.isAssignableFrom(c))
                return;

        throw new IllegalArgumentException(NO_BASIC_VIEW);
    }

    private void checkForBasicAttributes()
    {
        final Class<?> wanted = BasicFileAttributes.class;

        if (attributesHandles.containsKey(wanted))
            return;

        for (final Class<?> c: attributesHandles.keySet())
            if (wanted.isAssignableFrom(c))
                return;

        throw new IllegalArgumentException(NO_BASIC_ATTRS);
    }

    private void checkAttributesImplementations()
    {
        String viewName;
        Class<?> viewClass;
        Class<?> attributesClass;

        for (final Map.Entry<String, Class<?>> entry: definedViews.entrySet()) {
            viewName = entry.getKey();
            attributesClass = definedAttributes.get(viewName);
            if (attributesClass == null)
                continue;
            viewClass = entry.getValue();
            if (!viewHandles.containsKey(viewClass))
                continue;
            if (findMatchingAttrs(viewName) == null)
                throw new IllegalArgumentException(
                    String.format(VIEW_ATTR_NOIMPL, viewName)
                );
        }
    }

    private MethodHandle findMatchingAttrs(final String viewName)
    {
        Class<?> wanted;

        wanted = definedAttributes.get(viewName);
        MethodHandle handle;

        handle = attributesHandles.get(wanted);

        if (handle != null)
            return handle;

        String name;
        List<String> aliases;

        for (final Map.Entry<String, List<String>> entry:
            attributesAliases.entrySet()) {
            name = entry.getKey();
            aliases = entry.getValue();
            if (!aliases.contains(viewName))
                continue;
            wanted = definedAttributes.get(name);
            handle = attributesHandles.get(wanted);
            if (handle != null)
                return handle;
        }

        return null;
    }

    private void checkViewImplementations()
    {
        String attrName;
        Class<?> attributesClass;
        Class<?> viewClass;

        MethodHandle handle;

        for (final Map.Entry<String, Class<?>> entry:
            definedAttributes.entrySet()) {
            attributesClass = entry.getValue();
            handle = attributesHandles.get(attributesClass);
            if (handle == null)
                continue;
            attrName = entry.getKey();
            // TODO: at the moment this is required
            viewClass = definedViews.get(attrName);
            if (!viewHandles.containsKey(viewClass))
                throw new IllegalArgumentException(String.format(
                    ATTR_VIEW_NOIMPL, attrName
                ));
        }
    }

    private static MethodHandle getReaderWriterHandle(
        final Class<?> readerWriterClass, final Class<?> viewClass)
    {
        final MethodType type = MethodType.methodType(void.class, viewClass);

        try {
            return LOOKUP.findConstructor(readerWriterClass, type);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
}
