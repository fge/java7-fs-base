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

package com.github.fge.filesystem.driver.metadata.readers;

import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Test
public abstract class AttributeReaderTest<R extends AttributeReader<Object>>
{
    protected final Map<String, Object> values = new HashMap<>();

    protected final Path path = mock(Path.class);
    @SuppressWarnings("unchecked")
    protected final MetadataDriver<Object> driver = mock(MetadataDriver.class);

    protected R reader;

    @BeforeMethod
    public abstract void init()
        throws IOException;

    @Test
    public final void allAttributesTest()
        throws IOException
    {
        final Map<String, Object> map = reader.getAllAttributes();

        assertThat(map).isEqualTo(values);
    }
}
