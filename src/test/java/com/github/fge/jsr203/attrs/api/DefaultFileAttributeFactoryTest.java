package com.github.fge.jsr203.attrs.api;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class DefaultFileAttributeFactoryTest
{
    private DefaultFileAttributeFactory factory;

    @BeforeMethod
    public void initFactory()
    {
        factory = new DefaultFileAttributeFactory();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void addViewProviderTest()
        throws IOException
    {
        final Class<FileAttributeView> viewClass = FileAttributeView.class;
        final FileAttributeViewProvider<FileAttributeView> provider
            = mock(FileAttributeViewProvider.class);
        final Function<IOException, FileAttributeView> onFailure
            = mock(Function.class);

        final FileAttributeView expected = mock(FileAttributeView.class);

        when(provider.getView(any(Path.class))).thenReturn(expected);

        factory.addViewProvider(viewClass, provider, onFailure);

        final Path path = mock(Path.class);

        final FileAttributeView actual = factory.getViewByClass(viewClass,
            path);

        verify(provider).getView(same(path));
        verify(onFailure, never()).apply(any());

        assertThat(actual).isSameAs(expected);
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "addViewProviderTest")
    public void addViewProviderInvocationFailureTest()
        throws IOException
    {
        final Class<FileAttributeView> viewClass = FileAttributeView.class;
        final FileAttributeViewProvider<FileAttributeView> provider
            = mock(FileAttributeViewProvider.class);
        final Function<IOException, FileAttributeView> onFailure
            = mock(Function.class);

        final IOException exception = new IOException();
        when(provider.getView(any(Path.class))).thenThrow(exception);

        final FileAttributeView expected = mock(FileAttributeView.class);
        when(onFailure.apply(same(exception))).thenReturn(expected);

        factory.addViewProvider(viewClass, provider, onFailure);

        final Path path = mock(Path.class);

        final FileAttributeView actual = factory.getViewByClass(viewClass,
            path);

        verify(provider).getView(same(path));
        verify(onFailure, only()).apply(same(exception));

        assertThat(actual).isSameAs(expected);
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "addViewProviderTest")
    public void addViewProviderTwiceTest()
    {
        final Class<FileAttributeView> viewClass = FileAttributeView.class;
        final FileAttributeViewProvider<FileAttributeView> provider
            = mock(FileAttributeViewProvider.class);
        final Function<IOException, FileAttributeView> onFailure
            = mock(Function.class);

        factory.addViewProvider(viewClass, provider, onFailure);
        try {
            factory.addViewProvider(viewClass, provider, onFailure);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                DefaultFileAttributeFactory.PROVIDER_ALREADY_REGISTERED,
                viewClass.getSimpleName()
            ));
        }
    }

    @Test
    public void registerViewTest()
    {
        final String name = "foo";
        final Class<? extends FileAttributeView> viewClass
            = FileAttributeView.class;

        factory.registerView(name, viewClass);

        assertThat(factory.getViewClassForName(name)).isSameAs(viewClass);
    }

    @Test(dependsOnMethods = "registerViewTest")
    public void registerViewTwiceTest()
    {
        final String name = "foo";
        final Class<? extends FileAttributeView> viewClass
            = FileAttributeView.class;

        factory.registerView(name, viewClass);

        try {
            factory.registerView(name, viewClass);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                DefaultFileAttributeFactory.VIEW_ALREADY_REGISTERED, name
            ));
        }
    }

    @DataProvider
    public Iterator<Object[]> defaultRegisteredViews()
    {
        final List<Object[]> list = new ArrayList<>();

        String name;
        Class<? extends FileAttributeView> viewClass;

        name = StandardAttributeViewNames.BASIC;
        viewClass = BasicFileAttributeView.class;
        list.add(new Object[] { name, viewClass });

        name = StandardAttributeViewNames.POSIX;
        viewClass = PosixFileAttributeView.class;
        list.add(new Object[] { name, viewClass });

        name = StandardAttributeViewNames.DOS;
        viewClass = DosFileAttributeView.class;
        list.add(new Object[] { name, viewClass });

        name = StandardAttributeViewNames.OWNER;
        viewClass = FileOwnerAttributeView.class;
        list.add(new Object[] { name, viewClass });

        name = StandardAttributeViewNames.ACL;
        viewClass = AclFileAttributeView.class;
        list.add(new Object[] { name, viewClass });

        name = StandardAttributeViewNames.USER;
        viewClass = UserDefinedFileAttributeView.class;
        list.add(new Object[] { name, viewClass });

        Collections.shuffle(list);
        return list.iterator();
    }

    @Test(dataProvider = "defaultRegisteredViews")
    public void defaultRegisteredViewsTest(final String name,
        final Class<? extends FileAttributeView> viewClass)
    {
        assertThat(factory.getViewClassForName(name)).isSameAs(viewClass);
    }
}
