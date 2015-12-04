package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributesProvider;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAttributesFactory
    implements AttributesFactory
{
    // TODO: should be elsewhere?
    private static final Comparator<Class<?>> BESTFIT = (o1, o2) -> {
        if (Objects.equals(o1, o2))
            return 0;
        return o1.isAssignableFrom(o2) ? -1 : 1;
    };

    @VisibleForTesting
    static final String ATTRS_ALREADY_REGISTERED
        = "attribute class %s already registered with view name %s";

    @VisibleForTesting
    static final String ATTRS_NOT_REGISTERED
        = "attribute class %s is not registered";

    @VisibleForTesting
    static final String PROVIDER_ALREADY_REGISTERED
        = "a provider for attribute class %s already exists";

    private final Map<Class<? extends BasicFileAttributes>, String> nameMap
        = new HashMap<>();
    private final Map<Class<?>, AttributesProvider<?, ?>> providerMap
        = new HashMap<>();

    protected AbstractAttributesFactory()
    {
        registerAttributesByName(StandardAttributeViewNames.BASIC,
            BasicFileAttributes.class);
        registerAttributesByName(StandardAttributeViewNames.POSIX,
            PosixFileAttributes.class);
        registerAttributesByName(StandardAttributeViewNames.DOS,
            DosFileAttributes.class);
    }

    protected final <A extends BasicFileAttributes> void
        registerAttributesByName(final String viewName,
        final Class<A> attributesClass)
    {
        Objects.requireNonNull(viewName);
        Objects.requireNonNull(attributesClass);

        if (nameMap.put(attributesClass, viewName) != null)
            throw new IllegalArgumentException(String.format(
                ATTRS_ALREADY_REGISTERED, attributesClass.getSimpleName(),
                viewName));
    }

    protected final <A extends BasicFileAttributes, V extends FileAttributeView>
        void registerProvider(final Class<A> attributesClass,
        final AttributesProvider<V, A> provider)
    {
        if (!nameMap.containsKey(attributesClass))
            throw new IllegalArgumentException(String.format(
                ATTRS_NOT_REGISTERED, attributesClass.getSimpleName()
            ));

        if (providerMap.put(attributesClass, provider) != null)
            throw new IllegalArgumentException(String.format(
                PROVIDER_ALREADY_REGISTERED, attributesClass.getSimpleName()
            ));
    }

    @Override
    public <A extends BasicFileAttributes, V extends FileAttributeView> A
    getAttributesFromView(final V view, final Class<A> attributesClass)
        throws IOException
    {
        final Class<?> c = findBestCandidate(attributesClass);

        @SuppressWarnings("unchecked")
        final AttributesProvider<V, A> provider
            = (AttributesProvider<V, A>) providerMap.get(c);

        return provider.getAttributes(view);
    }

    // TODO: should be tested
    private Class<?> findBestCandidate(final Class<?> attributesClass)
    {
        return providerMap.keySet().stream()
            .filter(attributesClass::isAssignableFrom)
            .max(BESTFIT)
            .orElseThrow(IllegalStateException::new);
    }

    @VisibleForTesting
    String getViewNameForAttributesClass(
        final Class<? extends BasicFileAttributes> attributesClass)
    {
        return nameMap.get(attributesClass);
    }

    @VisibleForTesting
    AttributesProvider<?, ?> getProviderForClass(final Class<?> attributesClass)
    {
        return providerMap.get(attributesClass);
    }
}
