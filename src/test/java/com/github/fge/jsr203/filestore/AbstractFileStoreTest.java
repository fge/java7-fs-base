package com.github.fge.jsr203.filestore;

import com.github.fge.jsr203.attrs.factory.AttributeViewFactory;
import com.github.fge.jsr203.driver.FileSystemDriver;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.PosixFileAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AbstractFileStoreTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileSystemDriver driver;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private FileStore fileStore;

    @BeforeMethod
    public void initAttrName()
    {
        driver = mock(FileSystemDriver.class);
        fileStore = new TestFileStore(driver);
    }

    @Test
    public void defaultSizesTest()
        throws IOException
    {
        assertThat(fileStore.getTotalSpace()).isEqualTo(Long.MAX_VALUE);
        assertThat(fileStore.getUnallocatedSpace()).isEqualTo(Long.MAX_VALUE);
        assertThat(fileStore.getUsableSpace()).isEqualTo(Long.MAX_VALUE);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void fileAttributeTest()
    {
        final AttributeViewFactory viewFactory
            = mock(AttributeViewFactory.class);

        final String name = "foo";
        final Class viewClass = PosixFileAttributes.class;

        when(viewFactory.getViewClassByName(name))
            .thenReturn(viewClass);
        when(viewFactory.supportsViewClass(viewClass)).thenReturn(true);
        when(driver.getViewFactory()).thenReturn(viewFactory);

        assertThat(fileStore.supportsFileAttributeView(name)).isTrue();
        assertThat(fileStore.supportsFileAttributeView(viewClass)).isTrue();
    }
}
