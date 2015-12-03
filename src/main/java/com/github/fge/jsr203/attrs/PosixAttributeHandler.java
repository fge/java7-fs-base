package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.PosixFileAttributeView;

public class PosixAttributeHandler<V extends PosixFileAttributeView>
    extends BasicAttributeHandler<V>
{
    public PosixAttributeHandler(final V view)
    {
        super(view);

        addReader(StandardAttributeNames.OWNER, view::getOwner);
        addReader(StandardAttributeNames.GROUP,
            () -> view.readAttributes().group());
        addReader(StandardAttributeNames.PERMISSIONS,
            () -> view.readAttributes().permissions());
        addWriter(StandardAttributeNames.OWNER, view::setOwner);
        addWriter(StandardAttributeNames.GROUP, view::setGroup);
        addWriter(StandardAttributeNames.PERMISSIONS, view::setPermissions);
    }
}
