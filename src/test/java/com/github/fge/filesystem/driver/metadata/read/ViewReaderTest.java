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

package com.github.fge.filesystem.driver.metadata.read;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Test
public abstract class ViewReaderTest<R extends ViewReader<V>, V extends FileAttributeView>
{
    private final Class<V> viewClass;
    protected final Set<String> definedAttributes = new HashSet<>();

    protected V view;
    protected R reader;

    protected ViewReaderTest(final Class<V> viewClass, final String... names)
    {
        this.viewClass = viewClass;
        Collections.addAll(definedAttributes, names);
    }

    @BeforeMethod
    public final void init()
        throws IOException
    {
        initView();
        initReader();
    }

    protected abstract void initReader()
        throws IOException;

    @Test
    public final void allNamesTest()
        throws IOException
    {
        final Map<String, Object> map = reader.getAllAttributes();

        assertThat(map.keySet()).containsOnlyElementsOf(definedAttributes);
    }

    protected final void initView()
    {
        view = mock(viewClass);
    }
}
