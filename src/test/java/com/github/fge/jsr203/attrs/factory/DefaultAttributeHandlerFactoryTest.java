package com.github.fge.jsr203.attrs.factory;

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
import java.util.Iterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultAttributeHandlerFactoryTest
{
    private DefaultAttributeHandlerFactory factory;

    @BeforeMethod
    public void initFactory()
    {
        factory = new DefaultAttributeHandlerFactory();
    }

    @DataProvider
    public Iterator<Object[]> defaultHandlers()
    {
        return Stream.of(
            AclFileAttributeView.class,
            BasicFileAttributeView.class,
            DosFileAttributeView.class,
            FileOwnerAttributeView.class,
            PosixFileAttributeView.class,
            UserDefinedFileAttributeView.class
        ).map(c -> new Object[] { c }).iterator();
    }

    @Test(dataProvider = "defaultHandlers")
    public void defaultHandlersTest(final Class<? extends FileAttributeView> c)
    {
        assertThat(factory.getSupplierForView(c)).isNotNull();
    }
}
