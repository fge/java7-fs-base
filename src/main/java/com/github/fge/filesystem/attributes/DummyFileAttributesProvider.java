/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.attributes;

import java.io.IOException;

import com.github.fge.filesystem.attributes.provider.BasicFileAttributesProvider;


/**
 * DummyFileAttributesProvider.
 *
 * TODO ugly
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/06/10 umjammer initial version <br>
 */
public final class DummyFileAttributesProvider extends BasicFileAttributesProvider {

    /** */
    public static class DummyEntry {}

    /** */
    public DummyFileAttributesProvider() throws IOException {
        super();
    }

    @Override
    public boolean isRegularFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long size() {
        return 0;
    }
}
