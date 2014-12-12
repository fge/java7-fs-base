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
import com.github.fge.filesystem.attributes.descriptor.UserDefinedAttributesDescriptor;
import com.github.fge.filesystem.attributes.provider.FileAttributesProvider;
import com.github.fge.filesystem.exceptions.InvalidAttributeProviderException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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


    public FileAttributesFactory()
    {
        for (final AttributesDescriptor descriptor:
            StandardAttributesDescriptor.values())
            addDescriptor(descriptor);
        addDescriptor(UserDefinedAttributesDescriptor.INSTANCE);
    }

    @Nullable
    public final <V extends FileAttributeView> V getFileAttributeView(
        final Class<V> targetClass, final Object... args
    )
    {
        return getProviderInstance(targetClass, viewMap, args);
    }

    @Nullable
    public final <A extends BasicFileAttributes> A getFileAttributes(
        final Class<A> targetClass, final Object... args
    )
    {
        return getProviderInstance(targetClass, attrMap, args);
    }

    @Nullable
    public final AttributesDescriptor getDescriptor(final String name)
    {
        return descriptors.get(name);
    }

    protected final void addDescriptor(final AttributesDescriptor descriptor)
    {
        Objects.requireNonNull(descriptor);
        final String name = descriptor.getName();

        descriptors.put(name, descriptor);
        viewMap.put(name, descriptor.getViewClass());
        if (descriptor.getAttributeClass() != null)
            attrMap.put(name, descriptor.getAttributeClass());
    }

    protected final void addImplementation(final String name,
        final Class<? extends FileAttributesProvider> providerClass,
        final Class<?>... argTypes)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(providerClass);
        for (final Class<?> c: argTypes)
            Objects.requireNonNull(c);

        final AttributesDescriptor descriptor
            = descriptors.get(Objects.requireNonNull(name));

        if (descriptor == null)
            throw new InvalidAttributeProviderException("no descriptor for "
                + "attribute type " + name);

        checkCasts(providerClass, descriptor);
        providers.put(name, getConstructor(providerClass, argTypes));
    }

    @Nullable
    private <C> C getProviderInstance(final Class<C> targetClass,
        final Map<String, Class<?>> map, final Object... args)
    {
        final String name = getBestFit(targetClass, map);

        if (name == null)
            return null;

        final Object o;
        try {
            o = providers.get(name).invokeExact(args);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new InvalidAttributeProviderException("unable to build "
                + "attribute provider", throwable);
        }
        return targetClass.cast(o);
    }

    @Nullable
    private static String getBestFit(final Class<?> c,
        final Map<String, Class<?>> map)
    {
        String ret = null;
        Class<?> candidate, bestFit = null;

        for (final Map.Entry<String, Class<?>> entry: map.entrySet()) {
            candidate = entry.getValue();
            /*
             * We have an exact match: return
             */
            if (candidate == c)
                return entry.getKey();
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
                bestFit = candidate;
                ret = entry.getKey();
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

        c = descriptor.getAttributeClass();

        if (c != null && !c.isAssignableFrom(providerClass))
            throw new InvalidAttributeProviderException("provider class "
                + providerClass + " is not a subclass of " + c);
    }

    @Nonnull
    private static MethodHandle getConstructor(
        final Class<? extends FileAttributesProvider> providerClass,
        final Class<?>... argTypes
    )
    {
        final MethodHandle handle;
        try {
            handle = LOOKUP.findConstructor(providerClass,
                MethodType.methodType(void.class, argTypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new InvalidAttributeProviderException("no constructor found"
                + " for class " + providerClass + " with parameters "
                + Arrays.toString(argTypes), e);
        }

        final MethodType type = handle.type().changeReturnType(providerClass);
        return handle.asType(type);
    }
}
