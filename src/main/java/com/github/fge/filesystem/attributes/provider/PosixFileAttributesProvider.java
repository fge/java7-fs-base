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

package com.github.fge.filesystem.attributes.provider;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Objects;
import java.util.Set;

// TODO: use reflection/MethodHandle?
@ParametersAreNonnullByDefault
public abstract class PosixFileAttributesProvider<V extends PosixFileAttributeView>
    implements FileAttributesProvider<V, PosixFileAttributes>
{
    private final V view;

    protected PosixFileAttributesProvider(final V view)
    {
        this.view = Objects.requireNonNull(view);
    }

    @Nonnull
    @Override
    public final V getAttributeView()
    {
        return view;
    }

    @Nonnull
    @Override
    public final PosixFileAttributes getAttributes()
        throws IOException
    {
        return view.readAttributes();
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Nullable
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        final PosixFileAttributes attributes = getAttributes();

        switch (name) {
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

    @Override
    public final void setAttributeByName(final String name,
        @Nullable final Object value)
        throws IOException
    {
        switch (name) {
            /* basic */
            case "lastModifiedTime":
                view.setTimes((FileTime) value, null, null);
                break;
            case "lastAccessTime":
                view.setTimes(null, (FileTime) value, null);
                break;
            case "creationTime":
                view.setTimes(null, null,(FileTime) value);
                break;
            /* owner */
            case "owner":
                view.setOwner((UserPrincipal) value);
                break;
            /* posix */
            case "group":
                view.setGroup((GroupPrincipal) value);
                break;
            case "permissions":
                //noinspection unchecked
                view.setPermissions((Set<PosixFilePermission>) value);
                break;
            /* read only */
            case "size": case "isRegularFile": case "isDirectory":
            case "isSymbolicLink": case "isOther": case "fileKey":
                throw new ReadOnlyAttributeException(name);
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }
}
