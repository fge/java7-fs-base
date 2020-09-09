/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package com.github.fge.filesystem.driver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.FileStore;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.github.fge.filesystem.attributes.DummyFileAttributes;
import com.github.fge.filesystem.attributes.provider.DummyFileAttributesProvider;
import com.github.fge.filesystem.exceptions.IsDirectoryException;
import com.github.fge.filesystem.provider.FileSystemFactoryProvider;

import vavi.nio.file.UploadMonitor;
import vavi.nio.file.Util;
import vavi.util.Debug;


/**
 * ExtendedFileSystemDriverBase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2020/06/10 umjammer initial version <br>
 * @see UnixLikeFileSystemDriverBase
 */
@ParametersAreNonnullByDefault
public abstract class ExtendedFileSystemDriverBase extends UnixLikeFileSystemDriverBase {

    /** */
    protected ExtendedFileSystemDriverBase(final FileStore fileStore, final FileSystemFactoryProvider factoryProvider) {
        super(fileStore, factoryProvider);
    }

    /** */
    private UploadMonitor<DummyFileAttributes> uploadMonitor = new UploadMonitor<>();

    @Nonnull
    @Override
    public SeekableByteChannel newByteChannel(final Path path,
                                              final Set<? extends OpenOption> options,
                                              final FileAttribute<?>... attrs) throws IOException {
        if (options != null && Util.isWriting(options)) {
            uploadMonitor.start(path, new DummyFileAttributesProvider());
            return new Util.SeekableByteChannelForWriting(newOutputStream(path, options)) {
                @Override
                protected long getLeftOver() throws IOException {
                    long leftover = 0;
                    if (options.contains(StandardOpenOption.APPEND)) {
                        BasicFileAttributes entry = readAttributes(path, BasicFileAttributes.class);
                        if (entry != null && entry.size() >= 0) {
                            leftover = entry.size();
                        }
                    }
                    return leftover;
                }

                @Override
                public SeekableByteChannel position(long pos) throws IOException {
// TODO ad-hoc
if (pos < uploadMonitor.entry(path).size()) {
 throw new IOException("{\"@vavi\":" + uploadMonitor.entry(path).size() + "}");
}
                    return super.position(pos);
                }

                @Override
                public int write(ByteBuffer src) throws IOException {
                    int n = super.write(src);
                    uploadMonitor.entry(path).setSize(written);
                    return n;
                }

                @Override
                public void close() throws IOException {
                    try {
                        super.close();
                    } finally {
                        uploadMonitor.finish(path);
                    }
                }
            };
        } else {
            BasicFileAttributes entry = readAttributes(path, BasicFileAttributes.class);
            if (entry.isDirectory()) {
                throw new IsDirectoryException(path.toString());
            }
            return new Util.SeekableByteChannelForReading(newInputStream(path, null)) {
                @Override
                protected long getSize() throws IOException {
                    return entry.size();
                }
            };
        }
    }

    /* @see {@link #checkAccess(Path,AccessMode[]) */
    protected abstract void checkAccessImpl(final Path path, final AccessMode... modes) throws IOException;

    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        if (uploadMonitor.isUploading(path)) {
Debug.println("uploading... : " + path + ", " + uploadMonitor.entry(path));
            return;
        }

        checkAccessImpl(path, modes);
    }

    /* @see {@link #getPathMetadata(Path) */
    protected abstract Object getPathMetadataImpl(final Path path) throws IOException;

    @Nonnull
    @Override
    public Object getPathMetadata(final Path path) throws IOException {
        if (uploadMonitor.isUploading(path)) {
Debug.println("uploading... : " + path + ", " + uploadMonitor.entry(path));
            return uploadMonitor.entry(path);
        }

        return getPathMetadataImpl(path);
    }
}
