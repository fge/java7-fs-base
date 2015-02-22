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

package com.github.fge.filesystem.driver.oldmetadata.testclasses;

import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;
import com.github.fge.filesystem.driver.oldmetadata.views.FileOwnerMetadataView;

import java.nio.file.Path;

public final class MyFileOwnerMetadataView
    extends FileOwnerMetadataView<Object>
{
    public MyFileOwnerMetadataView(final Path path,
        final MetadataDriver<Object> driver)
    {
        super(path, driver);
    }
}