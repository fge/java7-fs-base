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
public final class DummyFileAttributes extends BasicFileAttributesProvider {

    /** */
    public DummyFileAttributes() throws IOException {
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

    private long size;

    @Override
    public long size() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "{dummy:{size: " + size + "}}";
    }
}
