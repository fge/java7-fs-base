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

package com.github.fge.filesystem.attributes.wrap.read;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class PosixFileAttributesReader
    extends FileAttributesReader<PosixFileAttributes>
{
    public PosixFileAttributesReader(final PosixFileAttributes attributes)
    {
        super(attributes);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Nullable
    @Override
    public Object readAttribute(final String name)
    {
        switch (Objects.requireNonNull(name)) {
            /* basic */
            case "lastModifiedTime":
                return attributes.lastModifiedTime();
            case "lastAccessTime":
                return attributes.lastAccessTime();
            case "creationTime":
                return attributes.creationTime();
            case "size":
                return attributes.size();
            case "isRegularFile":
                return attributes.isRegularFile();
            case "isDirectory":
                return attributes.isDirectory();
            case "isSymbolicLinke":
                return attributes.isSymbolicLink();
            case "isOther":
                return attributes.isOther();
            case "fileKey":
                return attributes.fileKey();
            /* owner */
            case "owner":
                return attributes.owner();
            /* posix */
            case "group":
                return attributes.group();
            case "permissions":
                return attributes.permissions();
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }
}
