package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributesProvider;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AbstractAttributesFactoryTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private AbstractAttributesFactory factory;

    @BeforeMethod
    public void initFactory()
    {
        factory = new TestAttributesFactory();
    }

    @Test
    public void registerAttributesByNameTest()
    {
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final String viewName = "foo";

        factory.registerAttributesByName(viewName, attributesClass);

        assertThat(factory.getViewNameForAttributesClass(attributesClass))
            .isEqualTo(viewName);
    }

    @Test(dependsOnMethods = "registerAttributesByNameTest")
    public void doubleRegisterAttributesByNameTest()
    {
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final String viewName = "foo";

        factory.registerAttributesByName(viewName, attributesClass);

        try {
            factory.registerAttributesByName(viewName, attributesClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.ATTRS_ALREADY_REGISTERED,
                attributesClass.getSimpleName(), viewName
            ));
        }
    }

    @DataProvider
    public Iterator<Object[]> nameMapData()
    {
        final List<Object[]> list = new ArrayList<>();

        Class<? extends BasicFileAttributes> attributesClass;
        String viewName;

        attributesClass = BasicFileAttributes.class;
        viewName = StandardAttributeViewNames.BASIC;
        list.add(new Object[] { attributesClass, viewName });

        attributesClass = PosixFileAttributes.class;
        viewName = StandardAttributeViewNames.POSIX;
        list.add(new Object[] { attributesClass, viewName });

        attributesClass = DosFileAttributes.class;
        viewName = StandardAttributeViewNames.DOS;
        list.add(new Object[] { attributesClass, viewName });

        Collections.shuffle(list);
        return list.iterator();
    }

    @Test(dataProvider = "nameMapData")
    public void defaultNameMapDataTest(
        final Class<? extends BasicFileAttributes> attributesClass,
        final String viewName)
    {
        assertThat(factory.getViewNameForAttributesClass(attributesClass))
            .isEqualTo(viewName);
    }

    @Test
    public void registerProviderNoDefinedClassTest()
    {
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final AttributesProvider<MyAttributeView, MyAttributes> provider
            = MyAttributeView::readAttributes;

        try {
            factory.registerProvider(attributesClass, provider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.ATTRS_NOT_REGISTERED,
                attributesClass.getSimpleName()
            ));
        }
    }

    @Test(dependsOnMethods = "registerProviderNoDefinedClassTest")
    public void registerProviderTest()
    {
        final String viewName = "foo";
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final AttributesProvider<MyAttributeView, MyAttributes> provider
            = MyAttributeView::readAttributes;

        factory.registerAttributesByName(viewName, attributesClass);
        factory.registerProvider(attributesClass, provider);

        assertThat(factory.getProviderForClass(attributesClass))
            .isSameAs(provider);
    }

    @Test(dependsOnMethods = "registerProviderTest")
    public void registerProviderTwiceTest()
    {
        final String viewName = "foo";
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final AttributesProvider<MyAttributeView, MyAttributes> provider
            = MyAttributeView::readAttributes;

        factory.registerAttributesByName(viewName, attributesClass);
        factory.registerProvider(attributesClass, provider);

        try {
            factory.registerProvider(attributesClass, provider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.PROVIDER_ALREADY_REGISTERED,
                attributesClass.getSimpleName()
            ));
        }
    }

    @Test(dependsOnMethods = "registerProviderTest")
    public void providerUsageTest()
        throws IOException
    {
        final String viewName = "foo";
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        final AttributesProvider<MyAttributeView, MyAttributes> provider
            = MyAttributeView::readAttributes;

        factory.registerAttributesByName(viewName, attributesClass);
        factory.registerProvider(attributesClass, provider);

        final MyAttributeView view = mock(MyAttributeView.class);
        final MyAttributes expected = mock(MyAttributes.class);
        when(view.readAttributes()).thenReturn(expected);

        final MyAttributes actual = factory.getAttributesFromView(view);
        assertThat(actual).isSameAs(expected);
    }
}
