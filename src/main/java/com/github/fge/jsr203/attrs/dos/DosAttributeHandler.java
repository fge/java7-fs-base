package com.github.fge.jsr203.attrs.dos;

import com.github.fge.jsr203.attrs.StandardAttributeNames;
import com.github.fge.jsr203.attrs.basic.BasicAttributeHandler;

import java.nio.file.attribute.DosFileAttributeView;

public class DosAttributeHandler<V extends DosFileAttributeView>
    extends BasicAttributeHandler<V>
{
    public DosAttributeHandler(final V view)
    {
        super(view);

        addReader(StandardAttributeNames.READONLY,
            () -> view.readAttributes().isReadOnly());
        addReader(StandardAttributeNames.HIDDEN,
            () -> view.readAttributes().isHidden());
        addReader(StandardAttributeNames.SYSTEM,
            () -> view.readAttributes().isSystem());
        addReader(StandardAttributeNames.ARCHIVE,
            () -> view.readAttributes().isArchive());
        addWriter(StandardAttributeNames.READONLY, view::setReadOnly);
        addWriter(StandardAttributeNames.HIDDEN, view::setHidden);
        addWriter(StandardAttributeNames.SYSTEM, view::setSystem);
        addWriter(StandardAttributeNames.ARCHIVE, view::setArchive);
    }
}
