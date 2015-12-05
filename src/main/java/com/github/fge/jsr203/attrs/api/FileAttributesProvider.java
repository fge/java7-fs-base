package com.github.fge.jsr203.attrs.api;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

@FunctionalInterface
public interface FileAttributesProvider<A extends BasicFileAttributes>
{
    A getAttributes(Path path, LinkOption... options)
        throws IOException;
}
