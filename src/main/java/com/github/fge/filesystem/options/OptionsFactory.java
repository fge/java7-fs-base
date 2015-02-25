/*
 * Copyright (c) 2015, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.filesystem.options;

import com.github.fge.filesystem.internal.VisibleForTesting;

import java.nio.file.CopyOption;
import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;

public final class OptionsFactory
{
    @VisibleForTesting
    static final String READ_OPTION_NOT_SUPPORTED
        = "unsupported read option %s";
    @VisibleForTesting
    static final String IS_WRITE_OPTION = "%s is a write option";
    @VisibleForTesting
    static final String WRITE_OPTION_NOT_SUPPORTED
        = "unsupported write option %s";
    @VisibleForTesting
    static final String IS_READ_OPTION = "%s is a read option";

    private final Set<OpenOption> defaultReadOptions;
    private final Set<OpenOption> supportedReadOptions;
    private final Set<OpenOption> defaultWriteOptions;
    private final Set<OpenOption> supportedWriteOptions;
    private final Set<CopyOption> supportedCopyOptions;

    public static OptionsFactoryBuilder newBuilder()
    {
        return new OptionsFactoryBuilder();
    }

    public static OptionsFactory defaultFactory()
    {
        return newBuilder().build();
    }

    public OptionsFactory(final OptionsFactoryBuilder builder)
    {
        defaultReadOptions = new HashSet<>(builder.defaultReadOptions);
        supportedReadOptions = new HashSet<>(builder.supportedReadOptions);
        defaultWriteOptions = new HashSet<>(builder.defaultWriteOptions);
        supportedWriteOptions = new HashSet<>(builder.supportedWriteOptions);
        supportedCopyOptions = new HashSet<>(builder.supportedCopyOptions);
    }

    public Set<OpenOption> toReadOptions(final Set<OpenOption> options)
    {
        for (final OpenOption option: options)
            checkValidReadOption(option);

        final Set<OpenOption> set = new HashSet<>(options);

        if (options.isEmpty())
            set.addAll(defaultReadOptions);

        set.add(READ);

        return Collections.unmodifiableSet(set);
    }

    public Set<OpenOption> toWriteOptions(final Set<OpenOption> options)
    {
        for (final OpenOption option: options)
            checkValidWriteOption(option);

        final Set<OpenOption> set = new HashSet<>(options);

        if (set.isEmpty())
            set.addAll(defaultWriteOptions);

        set.add(WRITE);

        return Collections.unmodifiableSet(set);
    }

    private void checkValidReadOption(final OpenOption option)
    {
        String errmsg;

        final boolean isWriteOption = supportedWriteOptions.contains(option);
        final boolean isReadOption = supportedReadOptions.contains(option);

        errmsg = String.format(IS_WRITE_OPTION, option);

        if (isWriteOption) {
            if (isReadOption)
                return;
            throw new IllegalArgumentException(errmsg);
        }

        errmsg = String.format(READ_OPTION_NOT_SUPPORTED, option);

        if (!isReadOption)
            throw new UnsupportedOperationException(errmsg);
    }

    private void checkValidWriteOption(final OpenOption option)
    {
        String errmsg;

        final boolean isReadOption = supportedReadOptions.contains(option);
        final boolean isWriteOption = supportedWriteOptions.contains(option);

        errmsg = String.format(IS_READ_OPTION, option);

        if (isReadOption) {
            if (isWriteOption)
                return;
            throw new IllegalArgumentException(errmsg);
        }

        errmsg = String.format(WRITE_OPTION_NOT_SUPPORTED, option);

        if (!isWriteOption)
            throw new UnsupportedOperationException(errmsg);
    }
}
