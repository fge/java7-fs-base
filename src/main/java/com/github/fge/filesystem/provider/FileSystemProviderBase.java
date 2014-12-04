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

package com.github.fge.filesystem.provider;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
public abstract class FileSystemProviderBase
    extends FileSystemProvider
{
    protected final FileSystemRepository repository;

    protected FileSystemProviderBase(final FileSystemRepository repository)
    {
        this.repository = Objects.requireNonNull(repository);
    }

    @Nonnull
    public final FileSystemRepository getRepository()
    {
        return repository;
    }

    /**
     * Returns the URI scheme that identifies this provider.
     *
     * @return The URI scheme
     */
    @Override
    public final String getScheme()
    {
        return repository.getScheme();
    }

    /**
     * Constructs a new {@code FileSystem} object identified by a URI. This
     * method is invoked by the {@link FileSystems#newFileSystem(URI, Map)}
     * method to open a new file system identified by a URI.
     * <p> The {@code uri} parameter is an absolute, hierarchical URI, with a
     * scheme equal (without regard to case) to the scheme supported by this
     * provider. The exact form of the URI is highly provider dependent. The
     * {@code env} parameter is a map of provider specific properties to
     * configure
     * the file system.
     * <p> This method throws {@link FileSystemAlreadyExistsException} if the
     * file system already exists because it was previously created by an
     * invocation of this method. Once a file system is {@link
     * FileSystem#close closed} it is provider-dependent if the
     * provider allows a new file system to be created with the same URI as a
     * file system it previously created.
     *
     * @param uri URI reference
     * @param env A map of provider specific properties to configure the file
     * system;
     * may be empty
     * @return A new file system
     *
     * @throws IllegalArgumentException If the pre-conditions for the {@code
     * uri} parameter aren't met,
     * or the {@code env} parameter does not contain properties required
     * by the provider, or a property value is invalid
     * @throws IOException An I/O error occurs creating the file system
     * @throws SecurityException If a security manager is installed and it
     * denies an unspecified
     * permission required by the file system provider implementation
     * @throws FileSystemAlreadyExistsException If the file system has
     * already been created
     */
    @Override
    public final FileSystem newFileSystem(final URI uri,
        final Map<String, ?> env)
        throws IOException
    {
        final URI normalized = Objects.requireNonNull(uri).normalize();
        return repository.createFileSystem(this, normalized,
            Collections.unmodifiableMap(env));
    }

    /**
     * Returns an existing {@code FileSystem} created by this provider.
     * <p> This method returns a reference to a {@code FileSystem} that was
     * created by invoking the {@link #newFileSystem(URI, Map) newFileSystem
     * (URI,Map)}
     * method. File systems created the {@link #newFileSystem(Path, Map)
     * newFileSystem(Path,Map)} method are not returned by this method.
     * The file system is identified by its {@code URI}. Its exact form
     * is highly provider dependent. In the case of the default provider the
     * URI's
     * path component is {@code "/"} and the authority, query and fragment
     * components
     * are undefined (Undefined components are represented by {@code null}).
     * <p> Once a file system created by this provider is {@link
     * FileSystem#close closed} it is provider-dependent if this
     * method returns a reference to the closed file system or throws {@link
     * FileSystemNotFoundException}. If the provider allows a new file system to
     * be created with the same URI as a file system it previously created then
     * this method throws the exception if invoked after the file system is
     * closed (and before a new instance is created by the {@link #newFileSystem
     * newFileSystem} method).
     * <p> If a security manager is installed then a provider implementation
     * may require to check a permission before returning a reference to an
     * existing file system. In the case of the {@link FileSystems#getDefault
     * default} file system, no permission check is required.
     *
     * @param uri URI reference
     * @return The file system
     *
     * @throws IllegalArgumentException If the pre-conditions for the {@code
     * uri} parameter aren't met
     * @throws FileSystemNotFoundException If the file system does not exist
     * @throws SecurityException If a security manager is installed and it
     * denies an unspecified
     * permission.
     */
    @Override
    public final FileSystem getFileSystem(final URI uri)
    {
        return repository.getFileSystem(uri);
    }
}
