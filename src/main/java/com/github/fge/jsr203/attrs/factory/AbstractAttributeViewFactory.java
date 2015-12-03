package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractAttributeViewFactory
    implements AttributeViewFactory
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

    private final Map<String, Class<? extends FileAttributeView>> viewMap
        = new HashMap<>();
    private final Map<Class<?>, Function<Path, ?>> providers = new HashMap<>();

    protected AbstractAttributeViewFactory()
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

    protected final <V extends FileAttributeView> void addClassByName(
        final String name, final Class<V> viewClass)
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
}
