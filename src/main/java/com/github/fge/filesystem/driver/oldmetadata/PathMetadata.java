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

package com.github.fge.filesystem.driver.oldmetadata;

import com.github.fge.filesystem.internal.NonFinalForTesting;

import java.nio.file.AccessMode;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

/**
 *
 * @param <M> metadata type parameter
 */
@NonFinalForTesting
public class PathMetadata<M>
{
    private final Path path;
    private final M metadata;
    private final Type type;
    private final EnumSet<AccessMode> accessModes;

    public PathMetadata(final Path path, final M metadata, final Type type,
        final EnumSet<AccessMode> accessModes)
    {
        this.path = path;
        this.metadata = metadata;
        this.type = type;
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.accessModes = accessModes;
    }

    public M getMetadata()
    {
        return metadata;
    }

    public Type getType()
    {
        return type;
    }

    public Set<AccessMode> getAccessModes()
    {
        return EnumSet.copyOf(accessModes);
    }

    public enum Type
    {
        REGULAR_FILE,
        DIR_EMPTY,
        DIR_NOTEMPTY,
        SYMLINK,
        OTHER,
        ;
    }
}
