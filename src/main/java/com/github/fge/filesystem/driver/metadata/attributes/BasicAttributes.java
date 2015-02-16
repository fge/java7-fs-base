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

package com.github.fge.filesystem.driver.metadata.attributes;

import com.github.fge.filesystem.driver.metadata.AttributesByName;
import com.github.fge.filesystem.driver.metadata.PathMetadata;
import com.github.fge.filesystem.exceptions.NoSuchAttributeException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @param <M> metadata class
 */
@SuppressWarnings("DesignForExtension")
@ParametersAreNonnullByDefault
public abstract class BasicAttributes<M>
    implements BasicFileAttributes, AttributesByName
{
    protected static final FileTime UNIX_EPOCH = FileTime.fromMillis(0L);

    protected final PathMetadata<M> pathMetadata;

    protected BasicAttributes(final PathMetadata<M> pathMetadata)
    {
        this.pathMetadata = pathMetadata;
    }

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
    public final boolean isRegularFile()
    {
        return pathMetadata.getType() == PathMetadata.Type.REGULAR_FILE;
    }

    @Override
    public final boolean isDirectory()
    {
        return pathMetadata.getType() == PathMetadata.Type.DIR_NOTEMPTY
            || pathMetadata.getType() == PathMetadata.Type.DIR_EMPTY;
    }

    @Override
    public final boolean isSymbolicLink()
    {
        return pathMetadata.getType() == PathMetadata.Type.SYMLINK;
    }

    @Override
    public final boolean isOther()
    {
        return pathMetadata.getType() == PathMetadata.Type.OTHER;
    }

    @Nullable
    @Override
    public Object fileKey()
    {
        return null;
    }

    @Override
    @SuppressWarnings("OverlyComplexMethod")
    public Object getAttributeByName(final String name)
    {
        switch (Objects.requireNonNull(name)) {
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
            default:
                throw new NoSuchAttributeException(name);
        }
    }

    @Override
    @Nonnull
    public Map<String, Object> getAllAttributes()
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

        return Collections.unmodifiableMap(map);
    }
}
