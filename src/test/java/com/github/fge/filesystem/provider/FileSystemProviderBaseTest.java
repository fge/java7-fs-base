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

package com.github.fge.filesystem.provider;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.spi.FileSystemProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.exceptions.IllegalOptionSetException;
import com.github.fge.filesystem.exceptions.UnsupportedOptionException;
import com.github.fge.filesystem.options.FileSystemOptionsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class FileSystemProviderBaseTest
{
    private FileSystemFactoryProvider factoryProvider;
    private FileSystemOptionsFactory optionsFactory;
    private FileSystemDriver driver;
    private FileSystemProvider provider;
    private Path path;

    @BeforeEach
    public void initMocks()
    {
        final FileSystemRepository repository
            = mock(FileSystemRepository.class);

        driver = mock(FileSystemDriver.class);
        when(repository.getDriver(any(Path.class))).thenReturn(driver);
        when(repository.getFactoryProvider()).thenReturn(factoryProvider);

        optionsFactory = spy(new FileSystemOptionsFactory());

        factoryProvider = new FileSystemFactoryProvider()
        {
            {
                setOptionsFactory(optionsFactory);
            }
        };
        when(repository.getFactoryProvider()).thenReturn(factoryProvider);

        provider = new FileSystemProviderBase(repository)
        {
        };

        path = mock(Path.class);
    }

    @Test
    public void writeOptionsAreRejectedOnNewInputStream()
        throws IOException
    {
        assertThrows(IllegalOptionSetException.class, () -> {
            provider.newInputStream(path, StandardOpenOption.WRITE);
        });

        //noinspection unchecked
        verify(driver, never())
            .newInputStream(any(Path.class), anySet());
    }

    @Test
    public void unknownReadOptionsAreRejectedOnNewInputStream()
        throws IOException
    {
        final OpenOption myopt = mock(OpenOption.class);
        when(myopt.toString()).thenReturn("foo");

        Exception e = assertThrows(UnsupportedOptionException.class, () -> {
            provider.newInputStream(path, myopt);
        });
        assertEquals("foo", e.getMessage());

        //noinspection unchecked
        verify(driver, never())
            .newInputStream(any(Path.class), anySet());
    }

    @Test
    public void readOptionsAreRejectedOnNewOutputStream()
        throws IOException
    {
        assertThrows(IllegalOptionSetException.class, () -> {
            provider.newOutputStream(path, StandardOpenOption.READ);
        });

        //noinspection unchecked
        verify(driver, never())
            .newOutputStream(any(Path.class), anySet());
    }

    @Test
    public void unknownWriteOptionsAreRejectedOnNewOutputStream()
        throws IOException
    {
        final OpenOption myopt = mock(OpenOption.class);
        when(myopt.toString()).thenReturn("foo");

        Exception e = assertThrows(UnsupportedOptionException.class, () -> {
            provider.newOutputStream(path, myopt);
        });
        assertEquals("foo", e.getMessage());

        //noinspection unchecked
        verify(driver, never())
            .newOutputStream(any(Path.class), anySet());
    }
}