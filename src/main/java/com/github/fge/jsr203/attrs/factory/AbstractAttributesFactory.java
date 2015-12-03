package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributesProvider;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractAttributesFactory
    implements AttributesFactory
{
    @VisibleForTesting
    static final String CLASS_ALREADY_MAPPED
        = "a class already exists for view name '%s'";

    @VisibleForTesting
    static final String VIEW_NOT_REGISTERED = "view class %s is not registered";

    @VisibleForTesting
    static final String PROVIDER_ALREADY_REGISTERED
        = "a provider already exists for view class %s";

    @VisibleForTesting
    static final String NO_PROVIDER = "no provider for view class %s";

    @VisibleForTesting
    static final String ATTRIBUTES_ALREADY_REGISTERED
        = "attributes class %s is already registerd";

    private final Map<String, Class<? extends FileAttributeView>> viewMap
        = new HashMap<>();
    private final Map<Class<? extends BasicFileAttributes>, Class<? extends FileAttributeView>> attributeMap
        = new HashMap<>();
    private final Map<Class<? extends FileAttributeView>, AttributesProvider<? extends FileAttributeView, ? extends BasicFileAttributes>>
        attributesProviders = new HashMap<>();

    private final Map<Class<? extends FileAttributeView>, Function<Path, ? extends FileAttributeView>>
        providers = new HashMap<>();

    protected AbstractAttributesFactory()
    {
        addClassByName(StandardAttributeViewNames.BASIC,
            BasicFileAttributeView.class);
        addClassByName(StandardAttributeViewNames.OWNER,
            FileOwnerAttributeView.class);
        addClassByName(StandardAttributeViewNames.ACL,
            AclFileAttributeView.class);
        addClassByName(StandardAttributeViewNames.POSIX,
            PosixFileAttributeView.class);
        addClassByName(StandardAttributeViewNames.USER,
            UserDefinedFileAttributeView.class);
        addClassByName(StandardAttributeViewNames.DOS,
            DosFileAttributeView.class);
    }

    protected final void addClassByName(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(viewClass);

        if (viewMap.put(name, viewClass) != null)
            throw new IllegalArgumentException(String.format(
                CLASS_ALREADY_MAPPED, name));
    }

    protected final <V extends FileAttributeView> void addImplementation(
        final Class<V> viewClass, final Function<Path, ? extends V> provider
    )
    {
        if (!viewMap.containsValue(viewClass))
            throw new IllegalArgumentException(String.format(
                VIEW_NOT_REGISTERED, viewClass.getSimpleName()));

        if (providers.put(viewClass, provider) != null)
            throw new IllegalArgumentException(String.format(
                PROVIDER_ALREADY_REGISTERED, viewClass.getSimpleName()
            ));
    }

    protected final <V extends FileAttributeView, A extends BasicFileAttributes>
        void registerAttributes(final Class<V> viewClass,
        final Class<A> attributesClass,
        final AttributesProvider<V, A> provider)
    {
        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(attributesClass);
        Objects.requireNonNull(provider);

        if (!viewMap.containsValue(viewClass))
            throw new IllegalArgumentException(String.format(
                VIEW_NOT_REGISTERED, viewClass.getSimpleName()));

        if (attributeMap.put(attributesClass, viewClass) != null)
            throw new IllegalArgumentException(String.format(
                ATTRIBUTES_ALREADY_REGISTERED, attributesClass.getSimpleName()
            ));

        attributesProviders.put(viewClass, provider);
    }

    @VisibleForTesting
    Class<? extends FileAttributeView> getViewClassForAttributeClass(
        final Class<? extends BasicFileAttributes> attributesClass)
    {
        return attributeMap.get(attributesClass);
    }

    @VisibleForTesting
    <V extends FileAttributeView> AttributesProvider<?, ?>
        getAttributesProviderForViewClass(final Class<V> viewClass)
    {
        return attributesProviders.get(viewClass);
    }

    @Override
    public final Class<? extends FileAttributeView> getViewClassByName(
        final String name)
    {
        return viewMap.get(name);
    }

    @Override
    public final <V extends FileAttributeView> V getView(final Class<V> viewClass,
        final Path path)
    {
        @SuppressWarnings("unchecked")
        final Function<Path, V> f
            = (Function<Path, V>) providers.get(viewClass);

        if (f == null)
            throw new UnsupportedOperationException(String.format(NO_PROVIDER,
                viewClass.getSimpleName()));

        return f.apply(path);
    }

    @Override
    public final <A extends BasicFileAttributes> A getAttributes(
        final Class<A> attributesClass, final Path path)
        throws IOException
    {
        final Class<? extends FileAttributeView> viewClass
            = attributeMap.get(attributesClass);
        final FileAttributeView view = getView(viewClass, path);
        @SuppressWarnings("unchecked")
        final AttributesProvider<FileAttributeView, A> provider
            = (AttributesProvider<FileAttributeView, A>) attributesProviders.get(viewClass);
        return provider.getAttributes(view);
    }
}
