package com.github.fge.jsr203.attrs.posix;

import com.github.fge.jsr203.attrs.basic.BasicFileAttributesBase;

import java.nio.file.attribute.PosixFileAttributes;

/**
 * Extension of {@link PosixFileAttributes} with default implementations
 *
 * <p>The default implementations are those of {@link BasicFileAttributesBase}
 * which this interface extends; no other methods have default implementations.
 * </p>
 */
public interface PosixFileAttributesBase
    extends PosixFileAttributes, BasicFileAttributesBase
{
}
