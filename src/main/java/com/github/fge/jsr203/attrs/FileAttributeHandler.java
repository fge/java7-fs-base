package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.AttributeReader;
import com.github.fge.jsr203.AttributeWriter;
import com.github.fge.jsr203.internal.VisibleForTesting;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class FileAttributeHandler<V extends FileAttributeView>
{
    @VisibleForTesting
    static final String NULL_ATTR_NAME = "attribute name cannot be null";
    @VisibleForTesting
    static final String NULL_READER = "reader cannot be null";
    @VisibleForTesting
    static final String DUPLICATE_READER
        = "there is already a reader for attribute name '%s'";
    @VisibleForTesting
    static final String NULL_WRITER = "writer cannot be null";
    @VisibleForTesting
    static final String DUPLICATE_WRITER
        = "there is already a writer for attribute name '%s'";

    protected final V view;

    private final Map<String, AttributeReader> readers = new HashMap<>();
    private final Map<String, AttributeWriter<?>> writers = new HashMap<>();

    protected FileAttributeHandler(final V view)
    {
        this.view = view;
    }

    public final String getViewName()
    {
        return view.name();
    }

    public final V getView()
    {
        return view;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public final void writeAttribute(final String name, final Object value)
        throws IOException
    {
        final AttributeWriter writer = writers.get(name);

        if (writer == null)
            throw new UnsupportedOperationException();

        writer.write(value);
    }

    public final Object readAttribute(final String name)
        throws IOException
    {
        final AttributeReader reader = readers.get(name);

        if (reader == null)
            throw new UnsupportedOperationException();

        return reader.read();
    }

    protected final void addReader(final String name,
        final AttributeReader reader)
    {
        Objects.requireNonNull(name, NULL_ATTR_NAME);
        Objects.requireNonNull(reader, NULL_READER);

        if (readers.put(name, reader) != null)
            throw new IllegalStateException(String.format(DUPLICATE_READER,
                name));
    }

    protected final <T> void addWriter(final String name,
        final AttributeWriter<T> writer)
    {
        Objects.requireNonNull(name, NULL_ATTR_NAME);
        Objects.requireNonNull(writer, NULL_WRITER);

        if (writers.put(name, writer) != null)
            throw new IllegalStateException(String.format(DUPLICATE_WRITER,
                name));
    }
}
