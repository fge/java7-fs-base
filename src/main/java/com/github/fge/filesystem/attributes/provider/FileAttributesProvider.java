package com.github.fge.filesystem.attributes.provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class FileAttributesProvider
    implements FileAttributeView
{
    private final String name;

    protected FileAttributesProvider(final String name)
        throws IOException
    {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public final String name()
    {
        return name;
    }

    public abstract void setAttributeByName(String name, Object value)
        throws IOException;

    @Nullable
    public abstract Object getAttributeByName(String name)
        throws IOException;

    @Nonnull
    public abstract Map<String, Object> getAllAttributes()
        throws IOException;
}
