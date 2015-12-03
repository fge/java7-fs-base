package com.github.fge.jsr203.attrs;

import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;

public final class StandardAttributesProviders
{
    private StandardAttributesProviders()
    {
        throw new Error("instantiation not permitted");
    }

    public static final AttributesProvider<BasicFileAttributeView, BasicFileAttributes>
        BASIC = BasicFileAttributeView::readAttributes;
    public static final AttributesProvider<DosFileAttributeView, DosFileAttributes>
        DOS = DosFileAttributeView::readAttributes;
    public static final AttributesProvider<PosixFileAttributeView, PosixFileAttributes>
        POSIX = PosixFileAttributeView::readAttributes;
}
