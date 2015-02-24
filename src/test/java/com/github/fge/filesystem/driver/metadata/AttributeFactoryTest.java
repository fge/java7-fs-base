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

import com.github.fge.filesystem.driver.metadata.testclasses.BasicAttrs;
import com.github.fge.filesystem.driver.metadata.testclasses.BasicView;
import com.github.fge.filesystem.driver.metadata.testclasses.DosAttrs;
import com.github.fge.filesystem.driver.metadata.testclasses.DosView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AttributeFactoryTest
{
    private interface DummyDriver
        extends MetadataDriver<Object>
    {
    }

    private DummyDriver driver;
    private AttributeFactoryBuilder<DummyDriver, Object> builder;

    private AttributeFactory<DummyDriver, Object> factory;

    @BeforeMethod
    public void init()
    {
        driver = mock(DummyDriver.class);
        builder = AttributeFactory.newBuilder(Object.class);
    }

    @Test
    public void viewInitByClass()
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final Object metadata = new Object();
        final Path path = mock(Path.class);

        when(driver.getMetadata(path)).thenReturn(metadata);

        final BasicFileAttributeView view
            = factory.getView(path, BasicFileAttributeView.class);

        assertThat(view).isInstanceOf(BasicView.class);
    }

    @Test
    public void viewInitBySuperclass()
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("dos", DosView.class)
            .addAttributesImplementation("dos", DosAttrs.class)
            .build();

        final Object metadata = new Object();
        final Path path = mock(Path.class);

        when(driver.getMetadata(path)).thenReturn(metadata);

        final BasicFileAttributeView view
            = factory.getView(path, BasicFileAttributeView.class);

        assertThat(view).isInstanceOf(DosView.class);
    }
}
