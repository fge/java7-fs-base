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

import com.github.fge.filesystem.exceptions.NoSuchAttributeException;
import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Provider for the {@code "dos"} file attribute view
 *
 * <p>The defaults are the same as those defined by {@link
 * BasicFileAttributesProvider}, with no other defaults added.</p>
 *
 * @see DosFileAttributeView
 * @see DosFileAttributes
 */
@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class DosFileAttributesProvider
    extends FileAttributesProvider
    implements DosFileAttributeView, DosFileAttributes
{
    protected static final FileTime UNIX_EPOCH = FileTime.fromMillis(0L);

    protected DosFileAttributesProvider()
        throws IOException
    {
        super("dos");
    }

    @Override
    public final DosFileAttributes readAttributes()
        throws IOException
    {
        return this;
    }

    /*
     * read
     */

    @Override
    public FileTime lastModifiedTime()
    {
        return UNIX_EPOCH;
    }

    @Override
    public FileTime lastAccessTime()
    {
        return UNIX_EPOCH;
    }

    @Override
    public FileTime creationTime()
    {
        return UNIX_EPOCH;
    }

    @Override
    public boolean isSymbolicLink()
    {
        return false;
    }

    @Override
    public boolean isOther()
    {
        return false;
    }

    @Override
    public Object fileKey()
    {
        return null;
    }

    /*
     * write
     */

    @Override
    public void setTimes(@Nullable final FileTime lastModifiedTime,
        @Nullable final FileTime lastAccessTime,
        @Nullable final FileTime createTime)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    @Override
    public void setArchive(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    @Override
    public void setSystem(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    @Override
    public void setHidden(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    @Override
    public void setReadOnly(final boolean value)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    /*
     * by name
     */

    @SuppressWarnings("OverlyLongMethod")
    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        Objects.requireNonNull(value);
        switch (Objects.requireNonNull(name)) {
            /* basic */
            case "lastModifiedTime":
                setTimes((FileTime) value, null, null);
                break;
            case "lastAccessTime":
                setTimes(null, (FileTime) value, null);
                break;
            case "creationTime":
                setTimes(null, null, (FileTime) value);
                break;
            /* dos */
            case "readonly":
                setReadOnly((Boolean) value);
                break;
            case "hidden":
                setHidden((Boolean) value);
                break;
            case "system":
                setSystem((Boolean) value);
                break;
            case "archive":
                setArchive((Boolean) value);
                break;
            case "size":
            case "isRegularFile":
            case "isDirectory":
            case "isSymbolicLink":
            case "isOther":
            case "fileKey":
                throw new ReadOnlyAttributeException(name);
            default:
                throw new NoSuchAttributeException(name);
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Nonnull
    @Override
    public final Object getAttributeByName(final String name)
        throws IOException
    {
        switch (Objects.requireNonNull(name)) {
            /* basic */
            case "lastModifiedTime":
                return lastModifiedTime();
            case "lastAccessTime":
                return lastAccessTime();
            case "creationTime":
                return creationTime();
            case "size":
                return size();
            case "isRegularFile":
                return isRegularFile();
            case "isDirectory":
                return isDirectory();
            case "isSymbolicLink":
                return isSymbolicLink();
            case "isOther":
                return isOther();
            case "fileKey":
                return fileKey();
            /* dos */
            case "readonly":
                return isReadOnly();
            case "hidden":
                return isHidden();
            case "system":
                return isSystem();
            case "archive":
                return isArchive();
            default:
                throw new NoSuchAttributeException(name);
        }
    }

    @Nonnull
    @Override
    public final Map<String, Object> getAllAttributes()
        throws IOException
    {
        final Map<String, Object> map = new HashMap<>();

        map.put("lastModifiedTime", lastModifiedTime());
        map.put("lastAccessTime", lastAccessTime());
        map.put("creationTime", creationTime());
        map.put("size", size());
        map.put("isRegularFile", isRegularFile());
        map.put("isDirectory", isDirectory());
        map.put("isSymbolicLink", isSymbolicLink());
        map.put("isOther", isOther());
        map.put("fileKey", fileKey());
        map.put("readonly", isReadOnly());
        map.put("hidden", isHidden());
        map.put("system", isSystem());
        map.put("archive", isArchive());

        return Collections.unmodifiableMap(map);
    }
}
