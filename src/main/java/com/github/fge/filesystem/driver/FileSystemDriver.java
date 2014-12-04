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

package com.github.fge.filesystem.driver;

import com.github.fge.filesystem.path.PathElements;
import com.github.fge.filesystem.path.PathElementsFactory;
import com.github.fge.filesystem.path.matchers.PathMatcherProvider;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Set;

public interface FileSystemDriver
    extends Closeable
{
    @Nonnull
    URI getUri();

    @Nonnull
    PathElementsFactory getPathElementsFactory();

    @Nonnull
    PathElements getRoot();

    @Nonnull
    FileStore getFileStore();

    @Nonnull
    Set<String> getSupportedFileAttributeViews();

    @Nonnull
    PathMatcherProvider getPathMatcherProvider();

    @Nonnull
    UserPrincipalLookupService getUserPrincipalLookupService();

    @Nonnull
    WatchService newWatchService();
}
