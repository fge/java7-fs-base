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

import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * A base implementation of {@link BasicFileAttributes}
 *
 * <p>This abstract class defines the following (overridable) defaults:</p>
 *
 * <ul>
 *     <li>all methods returning a {@link FileTime} return Unix epoch by default
 *     (ie, Jan 1st, 1970 at midnight, GMT); this is what those methods must
 *     return if a particular path/time combination is not supported;</li>
 *     <li>{@link #isSymbolicLink()} and {@link #isOther()} both return false;
 *     </li>
 *     <li>{@link #fileKey()} returns {@code null}.</li>
 * </ul>
 */
@SuppressWarnings("DesignForExtension")
public abstract class BasicFileAttributesBase
    implements BasicFileAttributes
{
    /**
     * Unix epoch as a {@link FileTime}
     */
    protected static final FileTime UNIX_EPOCH = FileTime.fromMillis(0L);

    /**
     * Returns the time of last modification.
     * <p> If the file system implementation does not support a time stamp
     * to indicate the time of last modification then this method returns an
     * implementation specific default value, typically a {@code FileTime}
     * representing the epoch (1970-01-01T00:00:00Z).
     *
     * @return a {@code FileTime} representing the time the file was last
     * modified
     */
    @Override
    public FileTime lastModifiedTime()
    {
        return UNIX_EPOCH;
    }

    /**
     * Returns the time of last access.
     * <p> If the file system implementation does not support a time stamp
     * to indicate the time of last access then this method returns
     * an implementation specific default value, typically the {@link
     * #lastModifiedTime() last-modified-time} or a {@code FileTime}
     * representing the epoch (1970-01-01T00:00:00Z).
     *
     * @return a {@code FileTime} representing the time of last access
     */
    @Override
    public FileTime lastAccessTime()
    {
        return UNIX_EPOCH;
    }

    /**
     * Returns the creation time. The creation time is the time that the file
     * was created.
     * <p> If the file system implementation does not support a time stamp
     * to indicate the time when the file was created then this method returns
     * an implementation specific default value, typically the {@link
     * #lastModifiedTime() last-modified-time} or a {@code FileTime}
     * representing the epoch (1970-01-01T00:00:00Z).
     *
     * @return a {@code FileTime} representing the time the file was created
     */
    @Override
    public FileTime creationTime()
    {
        return UNIX_EPOCH;
    }

    /**
     * Tells whether the file is a symbolic link.
     */
    @Override
    public boolean isSymbolicLink()
    {
        return false;
    }

    /**
     * Tells whether the file is something other than a regular file, directory,
     * or symbolic link.
     */
    @Override
    public boolean isOther()
    {
        return false;
    }

    /**
     * Returns an object that uniquely identifies the given file, or {@code
     * null} if a file key is not available. On some platforms or file systems
     * it is possible to use an identifier, or a combination of identifiers to
     * uniquely identify a file. Such identifiers are important for operations
     * such as file tree traversal in file systems that support <a
     * href="../package-summary.html#links">symbolic links</a> or file systems
     * that allow a file to be an entry in more than one directory. On UNIX file
     * systems, for example, the <em>device ID</em> and <em>inode</em> are
     * commonly used for such purposes.
     * <p> The file key returned by this method can only be guaranteed to be
     * unique if the file system and files remain static. Whether a file system
     * re-uses identifiers after a file is deleted is implementation
     * dependent and
     * therefore unspecified.
     * <p> File keys returned by this method can be compared for equality and
     * are
     * suitable for use in collections. If the file system and files remain
     * static,
     * and two files are the {@link Files#isSameFile same} with
     * non-{@code null} file keys, then their file keys are equal.
     *
     * @see Files#walkFileTree
     */
    @Override
    public Object fileKey()
    {
        return null;
    }
}
