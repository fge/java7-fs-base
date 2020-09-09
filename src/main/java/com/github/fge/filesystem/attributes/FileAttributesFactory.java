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

import com.github.fge.filesystem.attributes.descriptor.AttributesDescriptor;
import com.github.fge.filesystem.attributes.descriptor.StandardAttributesDescriptor;
import com.github.fge.filesystem.attributes.provider.BasicFileAttributesProvider;
import com.github.fge.filesystem.attributes.provider.FileAttributesProvider;
import com.github.fge.filesystem.driver.FileSystemDriverBase;
import com.github.fge.filesystem.exceptions.InvalidAttributeProviderException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Modifier;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * File attributes factory
 *
 * <p>This class allows you to register attribute providers and add
 * implementations. You must provide one such factory per filesystem and at
 * least provide an implementation of the {@link BasicFileAttributesProvider
 * basic file attributes}, as required by the API.</p>
 *
 * <p>It is also responsible for generating instances of attribute provider
 * classes at runtime (using {@link MethodHandle}s).</p>
 *
 * <p>You must extend this class, provide the metadata class used when
 * building attribute providers and then register your attribute provider
 * classes using {@link #addImplementation(String, Class)}; you can even
 * register your own attribute views using {@link
 * #addDescriptor(AttributesDescriptor)} (do this <em>before</em> registering
 * implementations).</p>
 *
 * <p>See <a
 * href="http://java7fs.wikia.com/wiki/Implementing_file_attributes">this
 * page</a>for a sample use.</p>
 *
 * <p>Unless otherwise noted, all methods of this class will throw a {@link
 * NullPointerException} if a null argument is passed.</p>
 *
 * @see FileSystemDriverBase
 */
@ParametersAreNonnullByDefault
public class FileAttributesFactory
{
    private static final MethodHandles.Lookup LOOKUP
        = MethodHandles.publicLookup();

    private final Map<String, AttributesDescriptor> descriptors
        = new HashMap<>();

    private final Map<String, Class<?>> viewMap = new HashMap<>();
    private final Map<String, Class<?>> attrMap = new HashMap<>();

    private final Map<String, MethodHandle> providers = new HashMap<>();

    private Class<?> metadataClass = null;

    /**
     * Constructor to extend
     */
    public FileAttributesFactory()
    {
        for (final AttributesDescriptor descriptor:
            StandardAttributesDescriptor.values())
            addDescriptor(descriptor);
    }

    public final boolean supportsFileAttributeView(
        final Class<? extends FileAttributeView> viewClass
    )
    {
        Objects.requireNonNull(viewClass);
        return getHandle(viewClass, viewMap) != null;
    }

    public final boolean supportsFileAttributeView(final String name)
    {
        final AttributesDescriptor descriptor
            = descriptors.get(Objects.requireNonNull(name));
        return descriptor != null
            && getHandle(descriptor.getViewClass(), viewMap) != null;
    }

    /**
     * Return a list of all attribute descriptors registered with this factory
     *
     * @return an immutable map (where keys are the names of the views and
     * values are the descriptors themselves)
     */
    @Nonnull
    public final Map<String, AttributesDescriptor> getDescriptors()
    {
        return Collections.unmodifiableMap(descriptors);
    }

    /**
     * Instantiate a new provider for a given attribute view with the given
     * metadata
     *
     * @param name the attribute view name
     * @param metadata the metadata to use to instantiate the provider
     * @return the provider, or {@code null} if this view is not supported
     * @throws IOException failed to generate the provider
     *
     * @see FileSystemDriverBase#readAttributes(Path, String, LinkOption...)
     * @see FileSystemDriverBase#setAttribute(Path, String, Object,
     * LinkOption...)
     * @see MethodHandle#invoke(Object...)
     */
    @Nullable
    public final FileAttributesProvider getProvider(final String name,
        final Object metadata)
        throws IOException
    {
        // TODO ugly
        if (DummyFileAttributes.class.isInstance(metadata)) {
            return (FileAttributesProvider) metadata;
        }

        final MethodHandle handle = providers.get(Objects.requireNonNull(name));

        if (handle == null)
            return null;

        try {
            return (FileAttributesProvider) handle.invoke(metadata);
        } catch (Error | RuntimeException | IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new InvalidAttributeProviderException(throwable);
        }
    }

    /**
     * Generate an attribute provider implementing the target attribute view
     * class
     *
     * <p>Note that the returned target may not "strictly" implement the view;
     * it may be a subclass of it. For instance, if you only have an
     * implementation for {@link DosFileAttributeView}, this is what will be
     * returned if you ask for a {@link BasicFileAttributeView} (since the
     * former extends the latter).</p>
     *
     * @param targetClass the target view
     * @param metadata the metadata to generate the provider with
     * @param <V> type parameter of the target view
     * @return a matching provider downcast to the given class, or {@code null}
     * if this view is not supported
     * @throws IOException failed to generate the attribute provider
     *
     * @see FileSystemDriverBase#getFileAttributeView(Path, Class,
     * LinkOption...)
     */
    @Nullable
    public final <V extends FileAttributeView> V getFileAttributeView(
        final Class<V> targetClass, final Object metadata
    )
        throws IOException
    {
        return getProviderInstance(targetClass, viewMap, metadata);
    }

    /**
     * Generate an attribute provider matching the target attributes class
     *
     * <p>Note that the returned target may not "strictly" implement the
     * attribute class; it may be a subclass of it. For instance, if you only
     * have an implementation for {@link DosFileAttributes}, this is what will
     * be returned if you ask for a {@link BasicFileAttributes} (since the
     * former extends the latter).</p>
     *
     * @param targetClass the target attribute class
     * @param metadata the metadata to generate the provider with
     * @param <A> type parameter of the target attribute class
     * @return a matching provider downcast to the given class, or {@code null}
     * if this attribute class is not supported
     * @throws IOException failed to generate the attribute provider
     *
     * @see FileSystemDriverBase#readAttributes(Path, Class, LinkOption...)
     */
    @Nullable
    public final <A extends BasicFileAttributes> A getFileAttributes(
        final Class<A> targetClass, final Object metadata
    )
        throws IOException
    {
        return getProviderInstance(targetClass, attrMap, metadata);
    }

    /**
     * Set the metadata class used when constructing new attribute provider
     * instances
     *
     * <p>This method <strong>must</strong> be called before registering
     * attribute provider implementations.</p>
     *
     * @param metadataClass the class
     * @throws IllegalArgumentException a metadata class has already been set
     *
     * @see FileSystemDriverBase#getPathMetadata(Path)
     */
    protected final void setMetadataClass(final Class<?> metadataClass)
    {
        //noinspection VariableNotUsedInsideIf
        if (this.metadataClass != null)
            throw new IllegalArgumentException("metadata class has already "
                + "been set");
        this.metadataClass = Objects.requireNonNull(metadataClass);
    }

    /**
     * Add an attribute view descriptor
     *
     * @param descriptor the descriptor to add
     * @throws IllegalArgumentException a descriptor by that name is already
     * registered
     *
     * @see AttributesDescriptor#getName()
     */
    protected final void addDescriptor(final AttributesDescriptor descriptor)
    {
        Objects.requireNonNull(descriptor);
        final String name = descriptor.getName();

        if (descriptors.containsKey(name))
            throw new IllegalArgumentException("a descriptor already exists "
                + "for view " + name);
        descriptors.put(name, descriptor);
        viewMap.put(name, descriptor.getViewClass());
        if (descriptor.getAttributeClass() != null)
            attrMap.put(name, descriptor.getAttributeClass());
    }

    /**
     * Add an implementation for a given attribute view
     *
     * @param name the name of the view
     * @param providerClass the attribute provider class
     * @throws IllegalArgumentException no metadata class has been set, or no
     * descriptor associated with that view
     * @throws InvalidAttributeProviderException provided class is not a
     * concrete class; or no suitable constructor has been found; or it is not a
     * subclass of the associated view class and (if any) attribute class
     *
     * @see AttributesDescriptor#getViewClass()
     * @see AttributesDescriptor#getAttributeClass()
     */
    protected final void addImplementation(final String name,
        final Class<? extends FileAttributesProvider> providerClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(providerClass);
        if (metadataClass == null)
            throw new IllegalArgumentException("metadata class has not been "
                + "set");

        final AttributesDescriptor descriptor
            = descriptors.get(Objects.requireNonNull(name));

        if (descriptor == null)
            throw new IllegalArgumentException("no descriptor for attribute"
                + " type " + name);

        checkCasts(providerClass, descriptor);
        providers.put(name, getConstructor(providerClass));
    }

    @Nullable
    private <C> C getProviderInstance(final Class<C> targetClass,
        final Map<String, Class<?>> map, final Object metadata)
        throws IOException
    {
        // TODO ugly
        if (DummyFileAttributes.class.isInstance(metadata)) {
            if (PosixFileAttributes.class.equals(targetClass)) {
                throw new UnsupportedOperationException("request posix for dummy");
            } else {
                return (C) metadata;
            }
        }

        final MethodHandle handle = getHandle(targetClass, map);

        if (handle == null)
            return null;

        try {
            //noinspection unchecked
            return (C) handle.invoke(metadata);
        } catch (Error | RuntimeException | IOException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new InvalidAttributeProviderException(throwable);
        }
    }

    @Nullable
    private MethodHandle getHandle(final Class<?> c,
        final Map<String, Class<?>> map)
    {
        MethodHandle ret = null;
        Class<?> candidate, bestFit = null;
        String name;

        for (final Map.Entry<String, Class<?>> entry: map.entrySet()) {
            name = entry.getKey();
            candidate = entry.getValue();
            /*
             * Test if the candidate is a subclass of the requested class;
             * if not, no luck, try next.
             */
            if (!c.isAssignableFrom(candidate))
                continue;
            /*
             * OK, it is a subclass. Test this against the best candidate we
             * have found for now, if any: if the new candidate is a superclass
             * of our current best, it is our new current best.
             */
            if (bestFit == null || candidate.isAssignableFrom(bestFit)) {
                if (!providers.containsKey(name))
                    continue;
                bestFit = candidate;
                ret = providers.get(name);
            }
        }

        return ret;
    }

    private static void checkCasts(
        final Class<? extends FileAttributesProvider> providerClass,
        final AttributesDescriptor descriptor)
    {
        final int modifiers = providerClass.getModifiers();

        if (!Modifier.isPublic(modifiers))
            throw new InvalidAttributeProviderException("provider class must "
                + "be public");

        if (Modifier.isAbstract(modifiers))
            throw new InvalidAttributeProviderException("provider class must "
                + "not be abstract");

        Class<?> c = descriptor.getViewClass();

        if (!c.isAssignableFrom(providerClass))
            throw new InvalidAttributeProviderException("provider class "
                + providerClass + " is not a subclass of " + c);

        //noinspection ReuseOfLocalVariable
        c = descriptor.getAttributeClass();

        if (c != null && !c.isAssignableFrom(providerClass))
            throw new InvalidAttributeProviderException("provider class "
                + providerClass + " is not a subclass of " + c);
    }

    @Nonnull
    private MethodHandle getConstructor(
        final Class<? extends FileAttributesProvider> providerClass
    )
    {
        final MethodHandle handle;
        try {
            handle = LOOKUP.findConstructor(providerClass,
                MethodType.methodType(void.class, metadataClass));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InvalidAttributeProviderException("no constructor found"
                + " for class " + providerClass + " with parameter "
                + metadataClass, e);
        }

        final MethodType type = handle.type().changeReturnType(providerClass);
        return handle.asType(type);
    }
}
