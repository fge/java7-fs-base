package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.FileTime;

public final class AttributeConstants
{
    private AttributeConstants()
    {
        throw new Error("no instantiation allowed");
    }

    public static final FileTime EPOCH = FileTime.fromMillis(0L);
}
