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

package com.github.fge.filesystem.driver.oldmetadata.writers;

import com.github.fge.filesystem.driver.oldmetadata.AttributeWriterByName;
import com.github.fge.filesystem.driver.oldmetadata.MetadataDriver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;

/**
 *
 * @param <M> metadata class
 */
@ParametersAreNonnullByDefault
public abstract class AttributeWriter<M>
    implements AttributeWriterByName
{
    protected final MetadataDriver<M> driver;
    protected final Path path;

    protected AttributeWriter(final Path path, final MetadataDriver<M> driver)
    {
        this.driver = driver;
        this.path = path;
    }
}
