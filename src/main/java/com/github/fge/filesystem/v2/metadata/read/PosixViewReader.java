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

package com.github.fge.filesystem.v2.metadata.read;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class PosixViewReader
    extends ViewReaderFixedNames<PosixFileAttributeView>
{
    public PosixViewReader(final PosixFileAttributeView view)
    {
        super(view, "lastModifiedTime", "lastAccessTime", "creationTime",
            "size", "isRegularFile", "isDirectory", "isSymbolicLink", "isOther",
            "fileKey", "owner", "group", "permissions");
    }

    @Nullable
    @Override
    public Object doGetAttributeByName(final String name)
        throws IOException
    {
        final PosixFileAttributes attrs = view.readAttributes();

        switch (Objects.requireNonNull(name)) {
            case "lastModifiedTime":
                return attrs.lastModifiedTime();
            case "lastAccessTime":
                return attrs.lastAccessTime();
            case "creationTime":
                return attrs.creationTime();
            case "size":
                return attrs.size();
            case "isRegularFile":
                return attrs.isRegularFile();
            case "isDirectory":
                return attrs.isDirectory();
            case "isSymbolicLink":
                return attrs.isSymbolicLink();
            case "isOther":
                return attrs.isOther();
            case "fileKey":
                return attrs.fileKey();
            case "owner":
                return attrs.owner();
            case "group":
                return attrs.group();
            case "permissions":
                return attrs.permissions();
        }

        throw new IllegalStateException();
    }
}
