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

import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class OptionsFactoryBuilder
{
    // Read options to include when none are specified (IN ADDITION to READ)
    private static final Set<OpenOption> DEFAULT_READ_OPTIONS;

    // Default supported read options (IN ADDITION to READ)
    private static final Set<OpenOption> DEFAULT_SUPPORTED_READ_OPTIONS;

    // Write options to include when none are specified (IN ADDITION to WRITE)
    private static final Set<OpenOption> DEFAULT_WRITE_OPTIONS;

    // Default supported write options (IN ADDITION to WRITE)
    private static final Set<OpenOption> DEFAULT_SUPPORTED_WRITE_OPTIONS;

    static {
        final Set<OpenOption> set = new HashSet<>();

        set.add(StandardOpenOption.CREATE);
        set.add(StandardOpenOption.TRUNCATE_EXISTING);

        DEFAULT_WRITE_OPTIONS = new HashSet<>(set);

        set.clear();

        set.addAll(DEFAULT_WRITE_OPTIONS);
        set.add(StandardOpenOption.CREATE_NEW);
        // Required; javadoc says to ignore it if not relevant for this fs
        set.add(StandardOpenOption.SPARSE);
        set.add(StandardOpenOption.WRITE);

        DEFAULT_SUPPORTED_WRITE_OPTIONS = new HashSet<>(set);

        set.clear();

        set.add(StandardOpenOption.READ);

        DEFAULT_READ_OPTIONS = new HashSet<>(set);

        set.clear();

        set.addAll(DEFAULT_READ_OPTIONS);

        DEFAULT_SUPPORTED_READ_OPTIONS = new HashSet<>(set);
    }

    @VisibleForTesting
    static final String IS_WRITE_OPTION
        = "option %s already registered as a write option";
    @VisibleForTesting
    static final String IS_READ_OPTION
        = "option %s already registered as a read option";

    final Set<OpenOption> defaultReadOptions
        = new HashSet<>(DEFAULT_READ_OPTIONS);
    final Set<OpenOption> supportedReadOptions
        = new HashSet<>(DEFAULT_SUPPORTED_READ_OPTIONS);

    final Set<OpenOption> defaultWriteOptions
        = new HashSet<>(DEFAULT_WRITE_OPTIONS);
    final Set<OpenOption> supportedWriteOptions
        = new HashSet<>(DEFAULT_SUPPORTED_WRITE_OPTIONS);

    OptionsFactoryBuilder()
    {
    }

    public OptionsFactoryBuilder addReadOption(final OpenOption option,
        final boolean byDefault)
    {
        Objects.requireNonNull(option);

        if (supportedWriteOptions.contains(option))
            throw new IllegalArgumentException(String.format(
                IS_WRITE_OPTION, option
            ));

        supportedReadOptions.add(option);

        if (byDefault)
            defaultReadOptions.add(option);

        return this;
    }

    public OptionsFactoryBuilder addWriteOption(final OpenOption option,
        final boolean byDefault)
    {
        Objects.requireNonNull(option);

        if (supportedReadOptions.contains(option))
            throw new IllegalArgumentException(String.format(
                IS_READ_OPTION, option
            ));

        supportedWriteOptions.add(option);

        if (byDefault)
            defaultWriteOptions.add(option);

        return this;
    }
}
