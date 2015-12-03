package com.github.fge.jsr203.attrs.basic;

import com.github.fge.jsr203.attrs.FixedNamesAttributeHandler;
import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

public class BasicAttributeHandler<V extends BasicFileAttributeView>
    extends FixedNamesAttributeHandler<V>
{
    public BasicAttributeHandler(final V view)
    {
        super(view);

        addReader(StandardAttributeNames.LAST_MODIFIED_TIME,
            () -> view.readAttributes().lastModifiedTime());
        addReader(StandardAttributeNames.LAST_ACCESS_TIME,
            () -> view.readAttributes().lastAccessTime());
        addReader(StandardAttributeNames.CREATION_TIME,
            () -> view.readAttributes().creationTime());
        addReader(StandardAttributeNames.SIZE,
            () -> view.readAttributes().size());
        addReader(StandardAttributeNames.IS_REGULAR_FILE,
            () -> view.readAttributes().isRegularFile());
        addReader(StandardAttributeNames.IS_DIRECTORY,
            () -> view.readAttributes().isDirectory());
        addReader(StandardAttributeNames.IS_SYMBOLIC_LINK,
            () -> view.readAttributes().isSymbolicLink());
        addReader(StandardAttributeNames.IS_OTHER,
            () -> view.readAttributes().isOther());
        addReader(StandardAttributeNames.FILE_KEY,
            () -> view.readAttributes().fileKey());
        addWriter(StandardAttributeNames.LAST_MODIFIED_TIME,
            (FileTime value) -> view.setTimes(value, null, null));
        addWriter(StandardAttributeNames.LAST_ACCESS_TIME,
            (FileTime value) -> view.setTimes(null, value, null));
        addWriter(StandardAttributeNames.CREATION_TIME,
            (FileTime value) -> view.setTimes(null, null, value));
    }
}
