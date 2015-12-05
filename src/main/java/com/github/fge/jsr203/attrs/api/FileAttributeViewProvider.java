package com.github.fge.jsr203.attrs.api;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttributeView;

@FunctionalInterface
public interface FileAttributeViewProvider<V extends FileAttributeView>
{
    V getView(Path path, LinkOption... options)
        throws IOException;
}
