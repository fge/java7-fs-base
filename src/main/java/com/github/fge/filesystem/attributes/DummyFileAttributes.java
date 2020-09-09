/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.attributes;

/**
 * DummyFileAttributesProvider.
 *
 * TODO ugly
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/06/10 umjammer initial version <br>
 */
public interface DummyFileAttributes {

    long size();

    void setSize(long size);
}
