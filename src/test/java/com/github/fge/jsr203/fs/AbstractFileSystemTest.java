package com.github.fge.jsr203.fs;

import com.github.fge.jsr203.filestore.AbstractFileStore;
import com.github.fge.jsr203.provider.AbstractFileSystemProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public final class AbstractFileSystemTest
{
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private AbstractFileStore fileStore;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private AbstractFileSystemProvider provider;

    @BeforeMethod
    public void initComponents()
    {
        fileStore = mock(AbstractFileStore.class);
        provider = mock(AbstractFileSystemProvider.class);
    }

    @Test
    public void initTest()
    {
        final AbstractFileSystem fs
            = spy(new TestFileSystem(fileStore, provider));

        assertThat(fs.isOpen()).isTrue();
        assertThat(fs.provider()).isSameAs(provider);
        assertThat(fs.getFileStores()).containsOnly(fileStore);
    }

    @Test(dependsOnMethods = "initTest")
    public void defaultsTest()
        throws IOException
    {
        final AbstractFileSystem fs
            = spy(new TestFileSystem(fileStore, provider));

        try {
            fs.getUserPrincipalLookupService();
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException ignored) {
        }

        try {
            fs.newWatchService();
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    @Test(dependsOnMethods = "initTest")
    public void closeTest()
        throws IOException
    {
        final AbstractFileSystem fs
            = spy(new TestFileSystem(fileStore, provider));

        fs.close();
        assertThat(fs.isOpen()).isFalse();
        verify(fs, times(1)).doClose();
    }

    @Test(dependsOnMethods = "closeTest")
    public void doubleCloseTest()
        throws IOException
    {
        final AbstractFileSystem fs
            = spy(new TestFileSystem(fileStore, provider));

        fs.close();
        fs.close();
        assertThat(fs.isOpen()).isFalse();
        verify(fs, times(1)).doClose();
    }
}
