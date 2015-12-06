package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DiscreteNamedAttributeDispatcher<V extends FileAttributeView>
    extends NamedAttributeDispatcher<V>
{
    @VisibleForTesting
    static final String READER_ALREADY_REGISTERED
        = "there is already a reader registered for attribute name '%s'";

    @VisibleForTesting
    static final String WRITER_ALREADY_REGISTERED
        = "there is already a writer registered for attribute name '%s'";

    @VisibleForTesting
    static final String NO_SUCH_READER
        = "cannot read attribute with name '%s'";

    @VisibleForTesting
    static final String NO_SUCH_WRITER
        = "cannot write attribute with name '%s'";

    private final Map<String, NamedAttributeReader> readers
        = new HashMap<>();
    private final Map<String, NamedAttributeWriter<?>> writers
        = new HashMap<>();

    public DiscreteNamedAttributeDispatcher(final V view)
    {
        super(view);
    }

    protected final void registerReader(final String name,
        final NamedAttributeReader reader)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(reader);

        if (readers.put(name, reader) != null)
            throw new IllegalArgumentException(String.format(
                READER_ALREADY_REGISTERED, name
            ));
    }

    protected final <T> void registerWriter(final String name,
        final NamedAttributeWriter<T> writer)
    {
        Objects.requireNonNull(name);
        Objects.requireNonNull(writer);

        if (writers.put(name, writer) != null)
            throw new IllegalArgumentException(String.format(
                WRITER_ALREADY_REGISTERED, name
            ));
    }

    @Override
    public final Object readByName(final String name)
        throws IOException
    {
        Objects.requireNonNull(name);
        final NamedAttributeReader reader = readers.get(name);

        if (reader == null)
            throw new UnsupportedOperationException(String.format(
                NO_SUCH_READER, name
            ));

        return reader.read();
    }

    @Override
    public final void writeByBame(final String name, final Object value)
        throws IOException
    {
        Objects.requireNonNull(name);

        @SuppressWarnings("unchecked")
        final NamedAttributeWriter<Object> writer
            = (NamedAttributeWriter <Object>) writers.get(name);

        if (writer == null)
            throw new UnsupportedOperationException(String.format(
                NO_SUCH_WRITER, name
            ));

        writer.write(value);
    }

    @Override
    public final Map<String, Object> readAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        for (final Map.Entry<String, NamedAttributeReader> entry:
            readers.entrySet())
            map.put(entry.getKey(), entry.getValue().read());

        return Collections.unmodifiableMap(map);
    }
}
