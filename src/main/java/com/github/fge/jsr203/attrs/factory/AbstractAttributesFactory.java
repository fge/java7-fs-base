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

public abstract class AbstractAttributesFactory
    implements AttributesFactory
{
    @VisibleForTesting
    static final String CLASS_ALREADY_MAPPED
        = "a class already exists for view name '%s'";

    private final Map<String, Class<? extends FileAttributeView>> classMap
        = new HashMap<>();

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
        // TODO
        return null;
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
