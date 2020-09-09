/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.attributes.provider;

import java.io.IOException;

import com.github.fge.filesystem.attributes.DummyFileAttributes;


/**
 * Provider for the {@code "dummy"} file attribute view
 */
public final class DummyFileAttributesProvider extends BasicFileAttributesProvider implements DummyFileAttributes {

    public DummyFileAttributesProvider() throws IOException {
        super();
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

    @Override
    public boolean isRegularFile() {
        return true;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
