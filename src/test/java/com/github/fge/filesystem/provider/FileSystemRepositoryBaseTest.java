/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.provider;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fge.filesystem.attributes.FileAttributesFactory;
import com.github.fge.filesystem.attributes.provider.BasicFileAttributesProvider;
import com.github.fge.filesystem.driver.FileSystemDriver;
import com.github.fge.filesystem.options.FileSystemOptionsFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * FileSystemRepositoryBaseTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/05/06 umjammer initial version <br>
 */
public class FileSystemRepositoryBaseTest {

    private class MyFileSystemRepository extends FileSystemRepositoryBase {
        public MyFileSystemRepository(String scheme, FileSystemFactoryProvider factoryProvider) {
            super(scheme, factoryProvider);
        }
        protected FileSystemDriver createDriver(URI uri, Map<String, ?> env) throws IOException {
            return null;
        }
        public Map<String, String> getParamsMap(URI uri) {
            return super.getParamsMap(uri);
        }
    }

    public static class MyBasicFileAttributesProvider extends BasicFileAttributesProvider {
        public MyBasicFileAttributesProvider(Object entry) throws IOException {
            super();
        }
        public boolean isRegularFile() {
            return false;
        }
        public boolean isDirectory() {
            return false;
        }
        public long size() {
            return 0;
        }
    }

    private MyFileSystemRepository repository;

    @BeforeEach
    public void init() {
        FileSystemFactoryProvider factoryProvider = new FileSystemFactoryProvider() {{
            setAttributesFactory(new FileAttributesFactory() {{
                setMetadataClass(Object.class);
                addImplementation("basic", MyBasicFileAttributesProvider.class);
            }});
            setOptionsFactory(new FileSystemOptionsFactory());
        }};
        repository = new MyFileSystemRepository("scheme", factoryProvider);
    }

    @Test
    public void testParamsMap() throws Exception {
        URI uri = URI.create("scheme:///?id=test");

        Map<String, String> params = repository.getParamsMap(uri);
        assertEquals("test", params.get("id"));
    }

    @Test
    public void testParamsMapWithoutValue() throws Exception {
        URI uri = URI.create("scheme:///?id=");

        Map<String, String> params = repository.getParamsMap(uri);
        assertNull(params.get("id"));
    }

    @Test
    public void testNoQueryParamsMap() throws Exception {
        URI uri = URI.create("scheme:///");

        Map<String, String> params = repository.getParamsMap(uri);
        assertNull(params.get("id"));
    }

    @Test
    public void testParamsMapWithSharp() throws Exception {
        URI uri = URI.create("scheme:///?id=test&second=2#sharp");

        Map<String, String> params = repository.getParamsMap(uri);
        assertEquals("test", params.get("id"));
        assertEquals("2", params.get("second"));
    }
}

/* */
