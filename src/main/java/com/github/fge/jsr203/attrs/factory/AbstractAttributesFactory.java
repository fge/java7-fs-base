package com.github.fge.jsr203.attrs.factory;

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

    private final Map<String, Class<? extends FileAttributeView>> classMap
        = new HashMap<>();
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

        if (classMap.put(name, viewClass) != null)
            throw new IllegalArgumentException(String.format(
                CLASS_ALREADY_MAPPED, name));
    }

    protected final <V extends FileAttributeView> void addImplementation(
        final Class<V> viewClass, final Function<Path, ? extends V> provider
    )
    {
        if (!classMap.containsValue(viewClass))
            throw new IllegalArgumentException(String.format(
                VIEW_NOT_REGISTERED, viewClass.getSimpleName()));

        if (providers.put(viewClass, provider) != null)
            throw new IllegalArgumentException(String.format(
                PROVIDER_ALREADY_REGISTERED, viewClass.getSimpleName()
            ));
    }

    @Override
    public Class<? extends FileAttributeView> getViewClassByName(
        final String name)
    {
        return classMap.get(name);
    }

    @Override
    public <V extends FileAttributeView> V getView(final Class<V> viewClass,
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
    public <A extends BasicFileAttributes> A getAttributes(
        final Class<A> attributesClass, final Path path)
        throws IOException
    {
        // TODO
        return null;
    }
}
