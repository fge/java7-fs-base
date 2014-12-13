package com.github.fge.filesystem.attributes.provider;

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;
import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Basic file attribute view provider class
 *
 * <p>Use this class if you want to implement your own file attribute view.See
 * <a href="http://java7fs.wikia.com/wiki/Implementing_file_attributes">this
 * page</a>for an example.</p>
 *
 * <p>Unless otherwise noted, all methods defined by this class do not accept
 * null arguments, and will throw a {@link NullPointerException} if a null
 * argument is passed as a parameter.</p>
 *
 * @see FileAttributeView
 */
@ParametersAreNonnullByDefault
public abstract class FileAttributesProvider
    implements FileAttributeView
{
    private final String name;

    /**
     * Protected constructor
     *
     * @param name the name of the view
     * @throws IOException failure to create the provider
     */
    // TODO: does not forbid an empty name, but ohwell
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

    /**
     * Set one attribute by name
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     * @throws IOException I/O error when trying to set the attribute
     * @throws ReadOnlyAttributeException the attribute is read only
     * @throws NoSuchAttributeException an attribute by this name does not exist
     * for this view
     */
    public abstract void setAttributeByName(String name, Object value)
        throws IOException;

    /**
     * Get an attribute value by name
     *
     * @param name the name of the attribute
     * @return the value of this attribute
     * @throws IOException I/O error when trying to set the attribute
     * @throws NoSuchAttributeException an attribute by this name does not exist
     * for this view
     */
    @Nullable
    public abstract Object getAttributeByName(String name)
        throws IOException;

    /**
     * Get all attributes for this view
     *
     * <p>The returned map will have the attribute names as keys and their
     * values as values. The returned map must be <em>immutable</em> (use
     * {@link Collections#unmodifiableMap(Map)} for instance).</p>
     *
     * <p>The order of keys in the returned map is not guaranteed.</p>
     *
     * @return an immutable map of all attribute name/value pairs
     * @throws IOException failure to read one or more attributes
     */
    @Nonnull
    public abstract Map<String, Object> getAllAttributes()
        throws IOException;
}
