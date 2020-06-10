/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.path.matchers;

import java.net.URI;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.fs.GenericFileSystem;
import com.github.fge.filesystem.provider.FileSystemFactoryProvider;
import com.github.fge.filesystem.provider.FileSystemRepository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * GlobPathMatcherTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/04/29 umjammer initial version <br>
 */
public class GlobPathMatcherTest {

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
    public void testGlobPathMatcher() {
        GlobPathMatcher gpm = new GlobPathMatcher("*.java");

        assertTrue(gpm.match("Foo.java"));
        assertTrue(gpm.match("Bar.java"));
        assertFalse(gpm.match("Buz.py"));
    }

    @Test
    public void testPathMacherOfGlobFromFs() {
        PathMatcher pm = fs.getPathMatcher("*.java");
        assertTrue(pm.matches(Paths.get("Foo.java")));
        assertTrue(pm.matches(Paths.get("Bar.java")));
        assertFalse(pm.matches(Paths.get("Buz.py")));
    }

    @Test
    public void testPathMacherOfRegexFromFs() {
        PathMatcher pm = fs.getPathMatcher("regex:A.+\\.java");
        assertTrue(pm.matches(Paths.get("Abc.java")));
        assertFalse(pm.matches(Paths.get("Zbc.java")));
    }
}

/* */
