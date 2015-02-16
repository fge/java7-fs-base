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

package com.github.fge.filesystem.driver.metadata.views;

import com.github.fge.filesystem.driver.metadata.AttributeWriterByName;
import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import com.github.fge.filesystem.driver.metadata.writers.PosixAttributeWriter;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

@ParametersAreNonnullByDefault
public class PosixMetadataView<M>
    implements PosixFileAttributeView, AttributeWriterByName
{
    private final String name = "posix";
    protected final Path path;
    protected final MetadataDriver<M> driver;
    protected final PosixAttributeWriter<M> writer;

    public PosixMetadataView(final Path path, final MetadataDriver<M> driver)
    {
        this.driver = driver;
        this.path = path;
        writer = driver.getAttributeWriter(path, name);
    }

    @Override
    public final String name()
    {
        return name;
    }

    @Override
    public final PosixFileAttributes readAttributes()
        throws IOException
    {
        return driver.getAttributesByName(path, name);
    }

    @Override
    public final UserPrincipal getOwner()
        throws IOException
    {
        return readAttributes().owner();
    }

    @Override
    public final void setAttributeByName(final String name, final Object value)
        throws IOException
    {
        writer.setAttributeByName(name, value);
    }

    @Override
    public final void setTimes(@Nullable final FileTime lastModifiedTime,
        @Nullable final FileTime lastAccessTime,
        @Nullable final FileTime createTime)
        throws IOException
    {
        writer.setTimes(lastModifiedTime, lastAccessTime, createTime);
    }

    @Override
    public final void setPermissions(final Set<PosixFilePermission> perms)
        throws IOException
    {
        writer.setPermissions(perms);
    }

    @Override
    public final void setGroup(final GroupPrincipal group)
        throws IOException
    {
        writer.setGroup(group);
    }

    @Override
    public final void setOwner(final UserPrincipal owner)
        throws IOException
    {
        writer.setOwner(owner);
    }
}
