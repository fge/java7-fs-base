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

package com.github.fge.filesystem.attributes.attrs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * Base abstract implementation of {@link PosixFileAttributes}
 *
 * <p>The provided defaults for {@link #permissions()} are to return {@code
 * "rwxr-xr-x"} (ie, 0755) for directories and {@code "rw-r--r--"} (ie, 0644)
 * for everything else.</p>
 *
 * <p>Note that this class gives access to all of {@link BasicFileAttributes}.
 * </p>
 */
public abstract class PosixFileAttributesBase
    extends BasicFileAttributesBase
    implements PosixFileAttributes
{
    protected static final Set<PosixFilePermission> DEFAULT_DIR_PERMS;
    protected static final Set<PosixFilePermission> DEFAULT_OTHER_PERMS;

    static {
        DEFAULT_DIR_PERMS = PosixFilePermissions.fromString("rwxr-xr-x");
        DEFAULT_OTHER_PERMS = PosixFilePermissions.fromString("rw-r--r--");
    }

    /**
     * Returns the permissions of the file. The file permissions are returned
     * as a set of {@link PosixFilePermission} elements. The returned set is a
     * copy of the file permissions and is modifiable. This allows the result
     * to be modified and passed to the {@link
     * PosixFileAttributeView#setPermissions
     * setPermissions} method to update the file's permissions.
     *
     * @return the file permissions
     *
     * @see PosixFileAttributeView#setPermissions
     */
    @Override
    public Set<PosixFilePermission> permissions()
    {
        return isDirectory() ? DEFAULT_DIR_PERMS : DEFAULT_OTHER_PERMS;
    }
}
