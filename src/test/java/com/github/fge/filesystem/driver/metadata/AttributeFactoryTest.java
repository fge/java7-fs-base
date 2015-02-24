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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

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
    public void viewInitByClassTest()
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final Path path = mock(Path.class);

        final BasicFileAttributeView view
            = factory.getView(path, BasicFileAttributeView.class);

        assertThat(view).isInstanceOf(BasicView.class);
    }

    @Test
    public void viewInitBySuperclassTest()
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("dos", DosView.class)
            .addAttributesImplementation("dos", DosAttrs.class)
            .build();

        final Path path = mock(Path.class);

        final BasicFileAttributeView view
            = factory.getView(path, BasicFileAttributeView.class);

        assertThat(view).isInstanceOf(DosView.class);
    }

    @Test
    public void viewInitNoSupportTest()
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final Path path = mock(Path.class);
        final Class<AclFileAttributeView> viewClass
            = AclFileAttributeView.class;

        try {
            factory.getView(path, viewClass);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                AttributeFactory.VIEW_CLASS_NOT_SUPPORTED,
                viewClass.getCanonicalName()
            ));
        }
    }

    @Test
    public void attrsInitByClassTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final Path path = mock(Path.class);

        final BasicFileAttributes attrs
            = factory.readAttributes(path, BasicFileAttributes.class);

        assertThat(attrs).isInstanceOf(BasicAttrs.class);
    }

    @Test
    public void attrsInitBySuperclassTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("dos", DosView.class)
            .addAttributesImplementation("dos", DosAttrs.class)
            .build();

        final Path path = mock(Path.class);

        final BasicFileAttributes attrs
            = factory.readAttributes(path, BasicFileAttributes.class);

        assertThat(attrs).isInstanceOf(DosAttrs.class);
    }

    @Test(dependsOnMethods = "attrsInitByClassTest")
    public void attrsInitIOExceptionTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final IOException exception = new IOException();

        doThrow(exception).when(driver).getMetadata(any(Path.class));

        try {
            factory.readAttributes(mock(Path.class), BasicFileAttributes.class);
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }

    @Test(dependsOnMethods = "attrsInitByClassTest")
    public void attrsInitNoSupportTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final Class<? extends BasicFileAttributes> attributesClass
            = PosixFileAttributes.class;

        try {
            factory.readAttributes(mock(Path.class), attributesClass);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                AttributeFactory.ATTRS_CLASS_NOT_SUPPORTED,
                attributesClass.getCanonicalName()
            ));
        }
    }

    @Test(dependsOnMethods = "attrsInitByClassTest")
    public void attrsInitByNameTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final BasicFileAttributes attrs
            = factory.readAttributes(mock(Path.class), "basic");

        assertThat(attrs).isInstanceOf(BasicAttrs.class);
    }

    @Test(dependsOnMethods = "attrsInitByNameTest")
    public void attrsInitByNameNoSupportTest()
        throws IOException
    {
        factory = builder.withDriver(driver)
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .build();

        final String name = "owner";

        try {
            factory.readAttributes(mock(Path.class), name);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                AttributeFactory.ATTRS_NOT_SUPPORTED, name
            ));
        }
    }
}
