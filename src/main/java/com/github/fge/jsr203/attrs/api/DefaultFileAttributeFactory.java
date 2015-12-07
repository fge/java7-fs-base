package com.github.fge.jsr203.attrs.api;

import com.github.fge.jsr203.attrs.api.byname.NamedAttributeDispatcher;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.LinkOption;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DefaultFileAttributeFactory
    implements FileAttributeFactory
{
    @VisibleForTesting
    static final String PROVIDER_ALREADY_REGISTERED
        = "a provider is already registered for view class %s";

    @VisibleForTesting
    static final String VIEW_ALREADY_REGISTERED
        = "view by name '%s' is already registered";

    private final Map<String, Class<? extends FileAttributeView>> viewMap
        = new HashMap<>();
    private final Map<String, List<String>> nameEquivalences = new HashMap<>();
    private final Map<Class<? extends FileAttributeView>, FileAttributeViewProvider<?>>
        viewProviders = new HashMap<>();
    private final Map<Class<? extends FileAttributeView>, Function<IOException, ? extends FileAttributeView>>
        failureProviders = new HashMap<>();

    public DefaultFileAttributeFactory()
    {
        registerView(StandardAttributeViewNames.BASIC,
            BasicFileAttributeView.class);
        registerView(StandardAttributeViewNames.POSIX,
            PosixFileAttributeView.class);
        registerView(StandardAttributeViewNames.DOS,
            DosFileAttributeView.class);
        registerView(StandardAttributeViewNames.OWNER,
            FileOwnerAttributeView.class);
        registerView(StandardAttributeViewNames.ACL,
            AclFileAttributeView.class);
        registerView(StandardAttributeViewNames.USER,
            UserDefinedFileAttributeView.class);
    }

    protected final void registerView(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(viewClass);

        if (viewMap.put(name, viewClass) != null)
            throw new IllegalArgumentException(String.format(
                VIEW_ALREADY_REGISTERED, name
            ));
    }

    protected final <V extends FileAttributeView> void addViewProvider(
        final Class<V> viewClass, final FileAttributeViewProvider<V> provider,
        final Function<IOException, V> onFailure)
    {
        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(provider);
        Objects.requireNonNull(onFailure);

        if (viewProviders.put(viewClass, provider) != null)
            throw new IllegalArgumentException(String.format(
                PROVIDER_ALREADY_REGISTERED, viewClass.getSimpleName()
            ));

        failureProviders.put(viewClass, onFailure);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends FileAttributeView> V getViewByClass(
        final Class<V> viewClass, final Path path, final LinkOption... options)
    {
        try {
            final FileAttributeViewProvider<V> provider
                = (FileAttributeViewProvider<V>) viewProviders.get(viewClass);
            return provider.getView(path, options);
        } catch (IOException e) {
            final Function<IOException, V> onFailure
                = (Function<IOException, V>) failureProviders.get(viewClass);
            return onFailure.apply(e);
        }
    }

    @Override
    public <A extends BasicFileAttributes> A getAttributesByClass(
        final Class<A> attributeClass, final Path path,
        final LinkOption... options)
        throws IOException
    {
        // TODO
        return null;
    }

    @Override
    public <V extends FileAttributeView> FileAttributeViewProvider<V>
    getFileAttributeViewProvider(final Class<V> viewClass)
    {
        // TODO
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends FileAttributeView> Class<V> getViewClassForName(
        final String viewName)
    {
        return (Class<V>) viewMap.get(viewName);
    }

    @Override
    public <V extends FileAttributeView> NamedAttributeDispatcher<V>
    getDispatcherForView(
        final V view)
    {
        // TODO
        return null;
    }
}
