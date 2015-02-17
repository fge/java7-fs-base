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

package com.github.fge.filesystem.driver.metadata.writers;

import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Random;

import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

public abstract class AttributeWriterTest<W extends AttributeWriter<Object>>
{
    protected final Path path = mock(Path.class);
    @SuppressWarnings("unchecked")
    protected MetadataDriver<Object> driver = mock(MetadataDriver.class);

    @SuppressWarnings("UnsecureRandomNumberGeneration")
    protected final long whatever = new Random().nextLong();
    protected final FileTime fileTime = FileTime.fromMillis(whatever);

    protected W writer;

    protected static FileTime nullFileTime()
    {
        return isNull(FileTime.class);
    }

    @BeforeMethod
    public abstract void init()
        throws IOException;
}
