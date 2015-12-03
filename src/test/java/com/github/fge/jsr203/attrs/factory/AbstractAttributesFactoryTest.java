package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.AttributesProvider;
import com.github.fge.jsr203.attrs.basic.BasicFileAttributeViewBase;
import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AbstractAttributesFactoryTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private AbstractAttributesFactory factory;

    @BeforeMethod
    public void initFactory()
    {
        factory = new TestAttributeFactory();
    }

    @Test
    public void addClassMapTest()
    {
        final String viewName = "foo";
        final Class<? extends FileAttributeView> viewClass
            = FileAttributeView.class;

        factory.addClassByName(viewName, viewClass);

        try {
            factory.addClassByName(viewName, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.CLASS_ALREADY_MAPPED, viewName
            ));
        }
    }

    @DataProvider
    public Iterator<Object[]> basicClassMapData()
    {
        final List<Object[]> list = new ArrayList<>();

        String viewName;
        Class<? extends FileAttributeView> viewClass;

        viewName = StandardAttributeViewNames.BASIC;
        viewClass = BasicFileAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        viewName = StandardAttributeViewNames.OWNER;
        viewClass = FileOwnerAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        viewName = StandardAttributeViewNames.ACL;
        viewClass = AclFileAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        viewName = StandardAttributeViewNames.POSIX;
        viewClass = PosixFileAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        viewName = StandardAttributeViewNames.USER;
        viewClass = UserDefinedFileAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        viewName = StandardAttributeViewNames.DOS;
        viewClass = DosFileAttributeView.class;
        list.add(new Object[] { viewName, viewClass });

        Collections.shuffle(list);
        return list.iterator();
    }

    @Test(
        dataProvider = "basicClassMapData",
        dependsOnMethods = "addClassMapTest"
    )
    public void basicClassMapTest(final String viewName,
        final Class<? extends FileAttributeView> viewClass)
    {
        assertThat(factory.getViewClassByName(viewName)).isSameAs(viewClass);
    }

    @Test
    public void addImplementationTest()
    {
        @SuppressWarnings("unchecked")
        final Function<Path, BasicFileAttributeView> provider
            = mock(Function.class);

        try {
            factory.addImplementation(FileAttributeView.class, provider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.VIEW_NOT_REGISTERED,
                FileAttributeView.class.getSimpleName()
            ));
        }

        factory.addImplementation(BasicFileAttributeView.class, provider);

        try {
            factory.addImplementation(BasicFileAttributeView.class, provider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.PROVIDER_ALREADY_REGISTERED,
                BasicFileAttributeView.class.getSimpleName()
            ));
        }
    }

    @Test(dependsOnMethods = "addImplementationTest")
    public void getViewTest()
    {
        final Class<BasicFileAttributeView> viewClass
            = BasicFileAttributeView.class;

        @SuppressWarnings("unchecked")
        final Function<Path, BasicFileAttributeView> provider
            = mock(Function.class);

        final Path path = mock(Path.class);
        final BasicFileAttributeViewBase expected
            = mock(BasicFileAttributeViewBase.class);

        when(provider.apply(path)).thenReturn(expected);

        factory.addImplementation(viewClass, provider);

        final BasicFileAttributeView actual = factory.getView(viewClass, path);

        assertThat(actual).isSameAs(expected);
    }

    @Test
    public void getViewFailureTest()
    {
        final Class<BasicFileAttributeView> viewClass
            = BasicFileAttributeView.class;
        final Path path = mock(Path.class);

        try {
            factory.getView(viewClass, path);
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.NO_PROVIDER, viewClass.getSimpleName()
            ));
        }
    }

    @Test(dependsOnMethods = "addClassMapTest")
    public void registerAttributesTest()
        throws IOException
    {
        final Class<MyAttributeView> viewClass = MyAttributeView.class;
        final Class<MyAttributes> attributesClass = MyAttributes.class;
        @SuppressWarnings("unchecked")
        final AttributesProvider<MyAttributeView, MyAttributes>
            attributesProvider = mock(AttributesProvider.class);

        try {
            factory.registerAttributes(viewClass, attributesClass,
                attributesProvider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.VIEW_NOT_REGISTERED,
                viewClass.getSimpleName()
            ));
        }

        final String viewName = "foo";

        factory.addClassByName(viewName, viewClass);

        factory.registerAttributes(viewClass, attributesClass, attributesProvider);

        assertThat(factory.getViewClassForAttributeClass(attributesClass))
            .isSameAs(viewClass);
        assertThat(factory.getAttributesProviderForViewClass(viewClass))
            .isSameAs(attributesProvider);

        try {
            factory.registerAttributes(viewClass, attributesClass, attributesProvider);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                AbstractAttributesFactory.ATTRIBUTES_ALREADY_REGISTERED,
                attributesClass.getSimpleName()
            ));
        }

        @SuppressWarnings("unchecked")
        final Function<Path, MyAttributeView> viewProvider
            = mock(Function.class);
        factory.addImplementation(MyAttributeView.class, viewProvider);

        final MyAttributeView view = mock(MyAttributeView.class);
        final MyAttributes expected = mock(MyAttributes.class);
        final Path path = mock(Path.class);
        when(viewProvider.apply(same(path))).thenReturn(view);
        when(attributesProvider.getAttributes(same(view))).thenReturn(expected);

        final MyAttributes actual = factory.getAttributes(MyAttributes.class,
            path);

        assertThat(actual).isSameAs(expected);
    }

    @DataProvider
    public Iterator<Object[]> attributesMapData()
    {
        final List<Object[]> list = new ArrayList<>();

        Class<? extends FileAttributeView> viewClass;
        Class<? extends BasicFileAttributes> attributesClass;
        AttributesProvider<?, ?> provider;

        Collections.shuffle(list);
        return list.iterator();
    }
}
