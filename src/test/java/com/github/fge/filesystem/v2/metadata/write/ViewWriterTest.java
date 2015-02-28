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

package com.github.fge.filesystem.v2.metadata.write;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;

import static org.mockito.Mockito.mock;

@Test
public abstract class ViewWriterTest<W extends ViewWriter<V>, V extends FileAttributeView>
{
    private final Class<V> viewClass;

    protected V view;
    protected W writer;

    protected ViewWriterTest(final Class<V> viewClass)
    {
        this.viewClass = viewClass;
    }

    @BeforeMethod
    public final void init()
        throws IOException
    {
        initView();
        initWriter();
    }

    protected abstract void initWriter()
        throws IOException;

    protected final void initView()
    {
        view = mock(viewClass);
    }
}
