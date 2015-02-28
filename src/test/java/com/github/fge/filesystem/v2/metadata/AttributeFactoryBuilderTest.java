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

package com.github.fge.filesystem.v2.metadata;

import com.github.fge.filesystem.v2.metadata.testclasses
    .AttrsBadConstructor;
import com.github.fge.filesystem.v2.metadata.testclasses
    .AttrsConstructorBadAccess;
import com.github.fge.filesystem.v2.metadata.testclasses.BasicAttrs;
import com.github.fge.filesystem.v2.metadata.testclasses.DosAttrs;
import com.github.fge.filesystem.v2.metadata.testclasses.DosView;
import com.github.fge.filesystem.v2.metadata.testclasses.FileOwnerView;
import com.github.fge.filesystem.v2.metadata.testclasses.ViewBadConstructor;
import com.github.fge.filesystem.v2.metadata.testclasses
    .ViewConstructorBadAccess;
import com.github.fge.filesystem.v2.metadata.testclasses.BasicView;
import com.github.fge.filesystem.v2.driver.MetadataDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class AttributeFactoryBuilderTest
{
    private interface DummyDriver
        extends MetadataDriver<Object>
    {
    }

    private AttributeFactoryBuilder<DummyDriver, Object> builder;

    @BeforeMethod
    public void init()
    {
        builder = new AttributeFactoryBuilder<>(Object.class);
    }

    @Test
    public void addAttrsBadNameTest()
    {
        final String name = "foo";
        final Class<? extends BasicFileAttributes> attributeClass
            = AttrsBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_ATTRIBUTES, name);

        try {
            builder.addAttributesImplementation("foo", attributeClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg);
        }
    }

    @Test
    public void addAttrsNotSubclassTest()
    {
        final String name = "posix";
        final Class<?> baseClass = PosixFileAttributes.class;
        final Class<? extends BasicFileAttributes> attributeClass
            = AttrsBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NOT_SUBCLASS,
            attributeClass.getCanonicalName(),
            baseClass.getCanonicalName());

        try {
            builder.addAttributesImplementation(name, attributeClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg);
        }
    }

    @Test
    public void addAttrsNoConstructorTest()
    {
        final String name = "basic";
        final Class<? extends BasicFileAttributes> attributeClass
            = AttrsBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_CONSTRUCTOR,
            attributeClass.getCanonicalName(),
            builder.attributesConstructor);

        try {
            builder.addAttributesImplementation(name, attributeClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg)
                .hasCauseExactlyInstanceOf(NoSuchMethodException.class);
        }
    }

    @Test
    public void addAttrsConstructorAccessProblemTest()
    {
        final String name = "basic";
        final Class<? extends BasicFileAttributes> attributeClass
            = AttrsConstructorBadAccess.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_CONSTRUCTOR,
            attributeClass.getCanonicalName(),
            builder.attributesConstructor);

        try {
            builder.addAttributesImplementation(name, attributeClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg)
                .hasCauseExactlyInstanceOf(IllegalAccessException.class);
        }

    }

    @Test
    public void addAttrsSuccessTest()
    {
        final String name = "basic";
        final Class<BasicFileAttributes> baseClass = BasicFileAttributes.class;

        final Class<? extends BasicFileAttributes> attributeClass
            = BasicAttrs.class;

        builder.addAttributesImplementation(name, attributeClass);

        assertThat(builder.attributesHandles).containsKey(baseClass);

        final MethodHandle handle = builder.attributesHandles.get(baseClass);

        final MethodType expected = builder.attributesConstructor
            .changeReturnType(attributeClass);
        assertThat(handle.type()).isEqualTo(expected);
    }

    @Test
    public void addViewBadNameTest()
    {
        final String name = "foo";
        final Class<? extends FileAttributeView> viewClass
            = ViewBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_VIEW, name);

        try {
            builder.addViewImplementation(name, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg);
        }
    }

    @Test
    public void addViewNotSubclassTest()
    {
        final String name = "posix";
        final Class<?> baseClass = PosixFileAttributeView.class;
        final Class<? extends FileAttributeView> viewClass
            = ViewBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NOT_SUBCLASS, viewClass.getCanonicalName(),
            baseClass.getCanonicalName());

        try {
            builder.addViewImplementation(name, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg);
        }
    }

    @Test
    public void addViewNoConstructorTest()
    {
        final String name = "basic";
        final Class<? extends FileAttributeView> viewClass
            = ViewBadConstructor.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_CONSTRUCTOR,
            viewClass.getCanonicalName(), builder.viewConstructor
        );

        try {
            builder.addViewImplementation(name, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg)
                .hasCauseExactlyInstanceOf(NoSuchMethodException.class);
        }
    }

    @Test
    public void addViewConstructorAccessProblemTest()
    {
        final String name = "basic";
        final Class<? extends FileAttributeView> viewClass
            = ViewConstructorBadAccess.class;
        final String errmsg = String.format(
            AttributeFactoryBuilder.NO_SUCH_CONSTRUCTOR,
            viewClass.getCanonicalName(), builder.viewConstructor
        );

        try {
            builder.addViewImplementation(name, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(errmsg)
                .hasCauseExactlyInstanceOf(IllegalAccessException.class);
        }
    }

    @Test
    public void addViewSuccessTest()
    {
        final String name = "basic";
        final Class<?> baseClass = BasicFileAttributeView.class;
        final Class<? extends FileAttributeView> viewClass = BasicView.class;

        builder.addViewImplementation(name, viewClass);

        assertThat(builder.viewHandles).containsKey(baseClass);

        final MethodType type = builder.viewConstructor
            .changeReturnType(viewClass);

        final MethodHandle handle = builder.viewHandles.get(baseClass);

        assertThat(handle.type()).isEqualTo(type);
    }

    @Test
    public void buildNoDriverTest()
    {
        try {
            builder.build();
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(AttributeFactoryBuilder.NO_DRIVER);
        }
    }

    @Test(dependsOnMethods = "buildNoDriverTest")
    public void buildNoBasicViewTest()
    {
        try {
            builder.withDriver(mock(DummyDriver.class)).build();
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(AttributeFactoryBuilder.NO_BASIC_VIEW);
        }
    }

    @Test(dependsOnMethods = "buildNoBasicViewTest")
    public void buildNoBasicAttrsTest()
    {
        try {
            builder.withDriver(mock(DummyDriver.class))
                .addViewImplementation("basic", BasicView.class)
                .build();
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(AttributeFactoryBuilder.NO_BASIC_ATTRS);
        }
    }

    @Test
    public void buildWithDerivedViewAndAttrsOkTest()
    {
        builder.withDriver(mock(DummyDriver.class))
            .addViewImplementation("dos", DosView.class)
            .addAttributesImplementation("dos", DosAttrs.class)
            .build();
    }

    @Test
    public void buildViewNoMatchingAttrs()
    {
        final String viewName = "dos";
        final Class<? extends FileAttributeView> viewClass = DosView.class;
        final String attrName = "basic";
        final Class<? extends BasicFileAttributes> attributeClass
            = BasicAttrs.class;

        try {
            builder.withDriver(mock(DummyDriver.class))
                .addAttributesImplementation(attrName, attributeClass)
                .addViewImplementation(viewName, viewClass)
                .build();
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AttributeFactoryBuilder.VIEW_ATTR_NOIMPL, viewName
            ));
        }
    }

    @Test
    public void buildAttrsNoMatchingView()
    {
        final String viewName = "basic";
        final Class<? extends FileAttributeView> viewClass = BasicView.class;
        final String attrName = "dos";
        final Class<? extends BasicFileAttributes> attributesClass
            = DosAttrs.class;

        try {
            builder.withDriver(mock(DummyDriver.class))
                .addAttributesImplementation(attrName, attributesClass)
                .addViewImplementation(viewName, viewClass)
                .build();
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AttributeFactoryBuilder.ATTR_VIEW_NOIMPL, attrName
            ));
        }
    }

    @Test
    public void buildViewWithoutAttrs()
    {
        builder.withDriver(mock(DummyDriver.class))
            .addViewImplementation("basic", BasicView.class)
            .addAttributesImplementation("basic", BasicAttrs.class)
            .addViewImplementation("owner", FileOwnerView.class)
            .build();
    }
}
