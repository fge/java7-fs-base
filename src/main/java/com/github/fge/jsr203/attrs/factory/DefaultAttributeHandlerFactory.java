package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributeHandler;
import com.github.fge.jsr203.attrs.acl.AclAttributeHandler;
import com.github.fge.jsr203.attrs.basic.BasicAttributeHandler;
import com.github.fge.jsr203.attrs.dos.DosAttributeHandler;
import com.github.fge.jsr203.attrs.owner.FileOwnerAttributeHandler;
import com.github.fge.jsr203.attrs.posix.PosixAttributeHandler;
import com.github.fge.jsr203.attrs.user.UserDefinedAttributeHandler;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class DefaultAttributeHandlerFactory
    implements AttributeHandlerFactory
{
    private static final String HANDLER_ALREADY_REGISTERED
        = "there is already a hander registered for view class %s";

    private static final String NO_HANDLER_FOUND
        = "cannot find a handler for class %s";

    // TODO: should be elsewhere?
    private static final Comparator<Class<?>> BESTFIT = (o1, o2) -> {
        if (Objects.equals(o1, o2))
            return 0;
        return o1.isAssignableFrom(o2) ? -1 : 1;
    };

    private final Map<Class<?>, Function<?, ?>> functionMap
        = new HashMap<>();

    public DefaultAttributeHandlerFactory()
    {
        registerHandler(AclFileAttributeView.class, AclAttributeHandler::new);
        registerHandler(BasicFileAttributeView.class,
            BasicAttributeHandler::new);
        registerHandler(DosFileAttributeView.class, DosAttributeHandler::new);
        registerHandler(FileOwnerAttributeView.class,
            FileOwnerAttributeHandler::new);
        registerHandler(PosixFileAttributeView.class,
            PosixAttributeHandler::new);
        registerHandler(UserDefinedFileAttributeView.class,
            UserDefinedAttributeHandler::new);
    }

    @Override
    public <V extends FileAttributeView> AttributeHandler<V> getHandlerForView(
        final V view)
    {
        @SuppressWarnings("unchecked")
        final Class<V> viewClass = (Class<V>) view.getClass();

        final Function<V, AttributeHandler<V>> function
            = getSupplierForView(viewClass);

        if (function == null)
            throw new UnsupportedOperationException();

        return function.apply(view);
    }

    protected final <V extends FileAttributeView> void registerHandler(
        final Class<V> viewClass,
        final Function<V, AttributeHandler<V>> function
    )
    {
        Objects.requireNonNull(viewClass);
        Objects.requireNonNull(function);

        if (functionMap.put(viewClass, function) != null)
            throw new IllegalArgumentException(String.format(
                HANDLER_ALREADY_REGISTERED, viewClass.getSimpleName()
            ));
    }

    @SuppressWarnings("unchecked")
    @VisibleForTesting
    <V extends FileAttributeView> Function<V, AttributeHandler<V>>
        getSupplierForView(final Class<V> viewClass)
    {
        final Optional<Class<?>> bestMatch = functionMap.keySet().stream()
            .filter(viewClass::isAssignableFrom)
            .max(BESTFIT);

        if (!bestMatch.isPresent())
            throw new UnsupportedOperationException(String.format(
                NO_HANDLER_FOUND, viewClass.getSimpleName()
            ));

        return (Function<V, AttributeHandler<V>>)
            functionMap.get(bestMatch.get());
    }
}
