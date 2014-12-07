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
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

// TODO: use reflection/MethodHandle?
@ParametersAreNonnullByDefault
public abstract class DosFileAttributesProvider<V extends DosFileAttributeView>
    implements FileAttributesProvider<V, DosFileAttributes>
{
    private final V view;

    protected DosFileAttributesProvider(final V view)
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
    public final DosFileAttributes getAttributes()
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
        final DosFileAttributes attributes = getAttributes();

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
            /* dos */
            case "readonly":
                return attributes.isReadOnly();
            case "hidden":
                return attributes.isHidden();
            case "system":
                return attributes.isSystem();
            case "archive":
                return attributes.isArchive();
            default:
                throw new IllegalStateException("how did I get there??");
        }
    }

    @SuppressWarnings({ "ConstantConditions", "OverlyLongMethod" })
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
            /* dos */
            case "readonly":
                view.setReadOnly((Boolean) value);
                break;
            case "hidden":
                view.setReadOnly((Boolean) value);
                break;
            case "system":
                view.setSystem((Boolean) value);
                break;
            case "archive":
                view.setArchive((Boolean) value);
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
