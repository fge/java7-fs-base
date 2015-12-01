package com.github.fge.jsr203;

import com.github.fge.jsr203.path.AbstractPath;

import java.nio.file.ProviderMismatchException;

/**
 * Unchecked exception of a higher level than {@link ProviderMismatchException}
 *
 * <p>It is expected that implementations of file systems using this package
 * will make use of {@link AbstractPath} for implementing paths. And this class
 * expects that other methods accepting paths as arguments have the same
 * filesystem. This exception is thrown if this is not the case.</p>
 */
public final class FileSystemMismatchException
    extends IllegalArgumentException
{
}
