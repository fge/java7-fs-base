/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.fs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.spi.FileSystemProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.provider.FileSystemFactoryProvider;
import com.github.fge.filesystem.provider.FileSystemRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class GenericFileSystemTest
{
    private final URI uri = URI.create("foo://bar");

    private GenericFileSystem fs;
    private FileSystemRepository repository;
    private FileSystemDriver driver;
    private FileSystemProvider provider;
    private FileSystemFactoryProvider factoryProvider;

    @BeforeEach
    public void init()
    {
        factoryProvider = new FileSystemFactoryProvider();
        repository = mock(FileSystemRepository.class);
        when(repository.getFactoryProvider()).thenReturn(factoryProvider);
        driver = mock(FileSystemDriver.class);
        provider = mock(FileSystemProvider.class);
        fs = new GenericFileSystem(uri, repository, driver, provider);
    }

    @Test
    public void newlyCreatedFileSystemIsOpen()
    {
        assertTrue(fs.isOpen());
    }

    @Test
    public void closedFileSystemClosesDriverAndUnregistersFromRepository()
        throws IOException
    {
//        when(driver.getUri()).thenReturn(uri);
        final InOrder inOrder = inOrder(repository, driver);

        fs.close();

        inOrder.verify(driver).close();
        inOrder.verify(repository).unregister(same(uri));

        assertFalse(fs.isOpen());
    }

    @Test
    public void filesystemIsUnregisteredEvenIfDriverFailsToClose()
        throws IOException
    {
//        final URI uri = URI.create("foo://bar");
        final IOException exception = new IOException("meh");

//        when(driver.getUri()).thenReturn(uri);
        doThrow(exception).when(driver).close();

        final InOrder inOrder = inOrder(repository, driver);

        assertThrows(IOException.class, () -> {
            fs.close();
        });

        inOrder.verify(driver).close();
        inOrder.verify(repository).unregister(uri);

        assertFalse(fs.isOpen());
    }
}