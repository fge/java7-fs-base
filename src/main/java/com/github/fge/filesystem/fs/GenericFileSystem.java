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

package com.github.fge.filesystem.fs;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.path.GenericPath;
import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.provider.FileSystemRepository;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Base {@link FileSystem} implementation
 *
 * <p>Limitations:</p>
 *
 * <ul>
 *     <li>only one {@link FileSystem#getRootDirectories() root directory}, only
 *     one {@link FileSystem#getFileStores() file store}.</li>
 * </ul>
 */
public final class GenericFileSystem
    extends FileSystem
{
    private volatile boolean open = true;

    private final FileSystemRepository repository;
    private final FileSystemProvider provider;
    private final FileSystemDriver driver;
    private final PathElementsFactory factory;
    private final String separator;

    public GenericFileSystem(final FileSystemRepository repository,
        final FileSystemDriver driver, final FileSystemProvider provider)
    {
        this.repository = Objects.requireNonNull(repository);
        this.driver = Objects.requireNonNull(driver);
        this.provider = Objects.requireNonNull(provider);
        factory = driver.getPathElementsFactory();
        separator = factory.getSeparator();
    }

    // TODO: move to PathElementsFactory
    @Nonnull
    public URI getUri()
    {
        return driver.getUri();
    }

    @Nonnull
    public FileSystemDriver getDriver()
    {
        return driver;
    }

    @Override
    public FileSystemProvider provider()
    {
        return provider;
    }

    @Override
    public void close()
        throws IOException
    {
        IOException exception = null;

        open = false;

        try {
            driver.close();
        } catch (IOException e) {
            exception = e;
        }

        repository.unregister(driver.getUri());

        if (exception != null)
            throw exception;
    }

    @Override
    public boolean isOpen()
    {
        return open;
    }

    @Override
    public boolean isReadOnly()
    {
        return driver.getFileStore().isReadOnly();
    }

    @Override
    public String getSeparator()
    {
        return separator;
    }

    @Override
    public Iterable<Path> getRootDirectories()
    {
        return Collections.<Path>singletonList(
            new GenericPath(this, factory, driver.getRoot())
        );
    }

    @Override
    public Iterable<FileStore> getFileStores()
    {
        return Collections.singletonList(driver.getFileStore());
    }

    @Override
    public Set<String> supportedFileAttributeViews()
    {
        return driver.getSupportedFileAttributeViews();
    }

    @SuppressWarnings("OverloadedVarargsMethod")
    @Override
    public Path getPath(final String first, final String... more)
    {
        if (more.length == 0)
            return new GenericPath(this, factory,
                factory.toPathElements(first));

        final StringBuilder sb = new StringBuilder(first);

        for (final String s: more)
            if (!s.isEmpty())
                sb.append(separator).append(s);

        final PathElements elements = factory.toPathElements(sb.toString());
        return new GenericPath(this, factory, elements);
    }

    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern)
    {
        final int index = Objects.requireNonNull(syntaxAndPattern).indexOf(':');

        final String type, arg;

        if (index == -1) {
            type = "glob";
            arg = syntaxAndPattern;
        } else {
            type = syntaxAndPattern.substring(0, index);
            arg = syntaxAndPattern.substring(index + 1);
        }

        return driver.getPathMatcherProvider().getPathMatcher(type, arg);
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService()
    {
        return driver.getUserPrincipalLookupService();
    }

    @Override
    public WatchService newWatchService()
        throws IOException
    {
        return driver.newWatchService();
    }
}
