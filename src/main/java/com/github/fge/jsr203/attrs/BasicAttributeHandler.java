package com.github.fge.jsr203.attrs;

import com.github.fge.jsr203.StandardAttributeNames;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

public final class BasicAttributeHandler
    extends FileAttributeHandlerWithAttributes<BasicFileAttributeView, BasicFileAttributes>
{
    public BasicAttributeHandler(final BasicFileAttributeView view)
    {
        super(view);

        addReader(StandardAttributeNames.LAST_MODIFIED_TIME,
            () -> getAttributes().lastModifiedTime());
        addReader(StandardAttributeNames.LAST_ACCESS_TIME,
            () -> getAttributes().lastAccessTime());
        addReader(StandardAttributeNames.CREATION_TIME,
            () -> getAttributes().creationTime());
        addReader(StandardAttributeNames.SIZE,
            () -> getAttributes().size());
        addReader(StandardAttributeNames.IS_REGULAR_FILE,
            () -> getAttributes().isRegularFile());
        addReader(StandardAttributeNames.IS_DIRECTORY,
            () -> getAttributes().isDirectory());
        addReader(StandardAttributeNames.IS_SYMBOLIC_LINK,
            () -> getAttributes().isSymbolicLink());
        addReader(StandardAttributeNames.IS_OTHER,
            () -> getAttributes().isOther());
        addReader(StandardAttributeNames.FILE_KEY,
            () -> getAttributes().fileKey());
        addWriter(StandardAttributeNames.LAST_MODIFIED_TIME,
            (FileTime value) -> view.setTimes(value, null, null));
        addWriter(StandardAttributeNames.LAST_ACCESS_TIME,
            (FileTime value) -> view.setTimes(null, value, null));
        addWriter(StandardAttributeNames.CREATION_TIME,
            (FileTime value) -> view.setTimes(null, null, value));
    }

    @Override
    public BasicFileAttributes getAttributes()
        throws IOException
    {
        return view.readAttributes();
    }
}
