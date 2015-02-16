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

package com.github.fge.filesystem.driver.metadata;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.attribute.FileAttributeView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;

public final class MetadataFactoryBuilderTest
{
    private MetadataFactoryBuilder builder;

    @BeforeMethod
    public void init()
    {
        builder = new MetadataFactoryBuilder();
    }

    @Test
    public void cannotAddAlreadyDefinedView()
    {
        try {
            builder.addDefinedView("basic", FileAttributeView.class);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e)
                .hasMessage("attribute view \"basic\" is already defined");
        }
    }

    @DataProvider
    public Iterator<Object[]> illegalViewNames()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { "" });
        list.add(new Object[] { ":ab" });
        list.add(new Object[] { "a:b" });
        list.add(new Object[] { "ab:" });

        return list.iterator();
    }


    @Test(dataProvider = "illegalViewNames")
    public void cannotAddIllegalViewName(final String name)
    {
        try {
            builder.addDefinedView(name, FileAttributeView.class);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e)
                .hasMessage(String.format("illegal view name \"%s\"", name));
        }
    }
}
