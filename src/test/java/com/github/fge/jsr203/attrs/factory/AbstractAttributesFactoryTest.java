package com.github.fge.jsr203.attrs.factory;

import com.github.fge.jsr203.attrs.constants.StandardAttributeViewNames;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;

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
}
