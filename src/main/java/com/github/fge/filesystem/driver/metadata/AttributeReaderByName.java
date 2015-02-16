/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.filesystem.driver.metadata;

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@ParametersAreNonnullByDefault
public interface AttributeReaderByName
{
    /**
     * Get an attribute value by name
     *
     * @param name the name of the attribute
     * @return the value of this attribute
     * @throws IOException I/O error when trying to read the attribute
     * @throws NoSuchAttributeException an attribute by this name does not exist
     * for this view
     */
    @Nullable
    Object getAttributeByName(String name)
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
    Map<String, Object> getAllAttributes()
        throws IOException;
}
