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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.fs.GenericFileSystem;
import com.github.fge.filesystem.provider.FileSystemFactoryProvider;
import com.github.fge.filesystem.provider.FileSystemRepository;

import static org.assertj.core.api.Assertions.assertThat;
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

    @BeforeMethod
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

        assertThat(gpm.match("Foo.java")).isTrue();
        assertThat(gpm.match("Bar.java")).isTrue();
        assertThat(gpm.match("Buz.py")).isFalse();
    }

    @Test
    public void testPathMacherOfGlobFromFs() {
        PathMatcher pm = fs.getPathMatcher("*.java");
        assertThat(pm.matches(Paths.get("Foo.java"))).isTrue();
        assertThat(pm.matches(Paths.get("Bar.java"))).isTrue();
        assertThat(pm.matches(Paths.get("Buz.py"))).isFalse();
    }

    @Test
    public void testPathMacherOfRegexFromFs() {
        PathMatcher pm = fs.getPathMatcher("regex:A.+\\.java");
        assertThat(pm.matches(Paths.get("Abc.java"))).isTrue();
        assertThat(pm.matches(Paths.get("Zbc.java"))).isFalse();
    }
}

/* */
