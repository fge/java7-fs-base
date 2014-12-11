/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.attributes;

import com.github.fge.filesystem.attributes.descriptor.AttributesDescriptor;
import com.github.fge.filesystem.attributes.provider.FileAttributesProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class FileAttributesDispatcher
{
    private final Map<String, AttributesDescriptor> descriptors
        = new HashMap<>();

    protected final void addDescriptor(final AttributesDescriptor descriptor)
    {
        Objects.requireNonNull(descriptor);
        descriptors.put(descriptor.getName(), descriptor);
    }

    protected final void addImplementation(final String name,
        final Class<? extends FileAttributesProvider> providerClass,
        final Class<?>... argTypes)
    {
    }
}
