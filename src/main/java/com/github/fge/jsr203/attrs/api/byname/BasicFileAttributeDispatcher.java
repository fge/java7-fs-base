package com.github.fge.jsr203.attrs.api.byname;

import com.github.fge.jsr203.attrs.constants.StandardAttributeNames;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

public class BasicFileAttributeDispatcher<V extends BasicFileAttributeView>
    extends DiscreteNamedAttributeDispatcher<V>
{
    public BasicFileAttributeDispatcher(final V view)
    {
        super(view);

        registerReader(StandardAttributeNames.LAST_MODIFIED_TIME,
            () -> view.readAttributes().lastModifiedTime());
        registerWriter(StandardAttributeNames.LAST_MODIFIED_TIME,
            (FileTime value) -> view.setTimes(value, null, null));

        registerReader(StandardAttributeNames.LAST_ACCESS_TIME,
            () -> view.readAttributes().lastAccessTime());
        registerWriter(StandardAttributeNames.LAST_ACCESS_TIME,
            (FileTime value) -> view.setTimes(null, value, null));

        registerReader(StandardAttributeNames.CREATION_TIME,
            () -> view.readAttributes().creationTime());
        registerWriter(StandardAttributeNames.CREATION_TIME,
            (FileTime value) -> view.setTimes(null, null, value));

        registerReader(StandardAttributeNames.SIZE,
            () -> view.readAttributes().size());

        registerReader(StandardAttributeNames.IS_REGULAR_FILE,
            () -> view.readAttributes().isRegularFile());

        registerReader(StandardAttributeNames.IS_DIRECTORY,
            () -> view.readAttributes().isDirectory());

        registerReader(StandardAttributeNames.IS_SYMBOLIC_LINK,
            () -> view.readAttributes().isSymbolicLink());

        registerReader(StandardAttributeNames.IS_OTHER,
            () -> view.readAttributes().isOther());

        registerReader(StandardAttributeNames.FILE_KEY,
            () -> view.readAttributes().fileKey());
    }
}
