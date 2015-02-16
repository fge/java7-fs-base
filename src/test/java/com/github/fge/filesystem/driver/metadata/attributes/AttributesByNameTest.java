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

package com.github.fge.filesystem.driver.metadata.attributes;

import com.github.fge.filesystem.driver.metadata.AttributesByName;
import com.github.fge.filesystem.driver.metadata.PathMetadata;
import com.github.fge.filesystem.exceptions.NoSuchAttributeException;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.AccessMode;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Test
public abstract class AttributesByNameTest
{
    private final Set<String> names = new HashSet<>();
    private final Class<? extends BasicFileAttributes> attributesClass;

    private final Path path = mock(Path.class);
    private final Object metadata = new Object();

    protected final PathMetadata<Object> pathMetadata = new PathMetadata<>(path,
        metadata, PathMetadata.Type.REGULAR_FILE,
        EnumSet.noneOf(AccessMode.class));

    protected AttributesByName attrs;

    protected AttributesByNameTest(
        final Class<? extends BasicFileAttributes> attributesClass,
        final String first, final String... other)
    {
        this.attributesClass = attributesClass;
        names.add(first);
        Collections.addAll(names, other);
    }

    @BeforeMethod
    protected abstract void initAttributes();

    @Test
    public final void attributesClassIsImplemented()
    {
        assertThat(attrs).isInstanceOf(attributesClass);
    }

    @DataProvider
    protected final Iterator<Object[]> names()
    {
        final List<Object[]> list = new ArrayList<>();

        for (final String name: names)
            list.add(new Object[] { name });

        return list.iterator();
    }

    @Test(dataProvider = "names")
    public final void attributeExists(final String name)
    {
        try {
            attrs.getAttributeByName(name);
        } catch (NoSuchAttributeException ignored) {
            Assertions.fail(String.format("attribute %s reported not to exist",
                name));
        }
    }

    @Test
    public final void mapContainsAllAttributes()
    {
        assertThat(attrs.getAllAttributes().keySet())
            .containsOnlyElementsOf(names);
    }
}
