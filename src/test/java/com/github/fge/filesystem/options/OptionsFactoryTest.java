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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.Set;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class OptionsFactoryTest
{
    private static final Set<OpenOption> NO_OPEN_OPTIONS
        = Collections.emptySet();

    private OptionsFactoryBuilder builder;
    private OptionsFactory factory;

    @BeforeMethod
    public void init()
    {
        builder = OptionsFactory.newBuilder();
    }

    @Test
    public void defaultReadOptionsTest()
    {
        factory = builder.build();

        final Set<OpenOption> options = factory.toReadOptions(NO_OPEN_OPTIONS);

        assertThat(options).containsOnly(READ);
    }

    @Test
    public void defaultReadOptionsWithAddedDefaultOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addReadOption(option, true).build();

        final Set<OpenOption> options = factory.toReadOptions(NO_OPEN_OPTIONS);

        assertThat(options).containsOnly(READ, option);
    }

    @Test
    public void defaultReadOptionsWithAddedNonDefaultOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addReadOption(option, false).build();

        final Set<OpenOption> options = factory.toReadOptions(NO_OPEN_OPTIONS);

        assertThat(options).containsOnly(READ);
    }

    @Test
    public void nonDefaultReadOptionsWithAddedOptionTest()
    {
        final OpenOption option1 = mock(OpenOption.class);
        final OpenOption option2 = mock(OpenOption.class);

        factory = builder.addReadOption(option1, false)
            .addReadOption(option2, true)
            .build();

        final Set<OpenOption> options
            = factory.toReadOptions(Collections.singleton(option1));

        assertThat(options).containsOnly(READ, option1);
    }

    @Test
    public void readOptionsWithNonSupportedOptionTest()
    {
        factory = builder.build();

        final OpenOption option = mock(OpenOption.class);

        try {
            factory.toReadOptions(Collections.singleton(option));
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactory.READ_OPTION_NOT_SUPPORTED, option
            ));
        }
    }

    @Test
    public void readOptionsWithWriteOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addWriteOption(option, false).build();

        try {
            factory.toReadOptions(Collections.singleton(option));
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactory.IS_WRITE_OPTION, option
            ));
        }
    }

    @Test
    public void readOptionsWithReadWriteOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addReadWriteOption(option, false).build();

        final Set<OpenOption> options
            = factory.toReadOptions(Collections.singleton(option));

        assertThat(options).containsOnly(READ, option);
    }

    @Test
    public void defaultWriteOptionsTest()
    {
        factory = builder.build();

        final Set<OpenOption> set = factory.toWriteOptions(NO_OPEN_OPTIONS);

        assertThat(set).containsOnly(WRITE, TRUNCATE_EXISTING, CREATE);
    }

    @Test
    public void defaultWriteOptionsWithAddedDefaultOptionsTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addWriteOption(option, true).build();

        final Set<OpenOption> set = factory.toWriteOptions(NO_OPEN_OPTIONS);

        assertThat(set).containsOnly(WRITE, TRUNCATE_EXISTING, CREATE, option);
    }

    @Test
    public void defaultWriteOptionsWithAddedNonDefaultOptionsTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addWriteOption(option, false).build();

        final Set<OpenOption> set = factory.toWriteOptions(NO_OPEN_OPTIONS);

        assertThat(set).containsOnly(WRITE, TRUNCATE_EXISTING, CREATE);
    }

    @Test
    public void nonDefaultWriteOptionsWithAddedOptionTest()
    {
        final OpenOption option1 = mock(OpenOption.class);
        final OpenOption option2 = mock(OpenOption.class);

        factory = builder.addWriteOption(option1, false)
            .addWriteOption(option2, true)
            .build();

        final Set<OpenOption> options
            = factory.toWriteOptions(Collections.singleton(option1));

        assertThat(options).containsOnly(WRITE, option1);
    }

    @Test
    public void writeOptionsWithNonSupportedOptionTest()
    {
        factory = builder.build();

        final OpenOption option = mock(OpenOption.class);

        try {
            factory.toWriteOptions(Collections.singleton(option));
            shouldHaveThrown(UnsupportedOperationException.class);
        } catch (UnsupportedOperationException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactory.WRITE_OPTION_NOT_SUPPORTED, option
            ));
        }
    }

    @Test
    public void writeOptionsWithReadOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addReadOption(option, false).build();

        try {
            factory.toWriteOptions(Collections.singleton(option));
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactory.IS_READ_OPTION, option
            ));
        }
    }

    @Test
    public void writeOptionsWithReadWriteOptionTest()
    {
        final OpenOption option = mock(OpenOption.class);

        factory = builder.addReadWriteOption(option, false).build();

        final Set<OpenOption> options
            = factory.toWriteOptions(Collections.singleton(option));

        assertThat(options).containsOnly(WRITE, option);
    }
}
