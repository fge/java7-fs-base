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
import java.nio.file.StandardOpenOption;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public final class OptionsFactoryBuilderTest
{
    private OptionsFactoryBuilder builder;

    @BeforeMethod
    public void init()
    {
        builder = new OptionsFactoryBuilder();
    }

    @Test
    public void addReadOptionNoDefaultTest()
    {
        final OpenOption option = mock(OpenOption.class);

        builder.addReadOption(option, false);

        assertThat(builder.supportedReadOptions).contains(option);
        assertThat(builder.defaultReadOptions).doesNotContain(option);
    }

    @Test
    public void addReadOptionDefaultTest()
    {
        final OpenOption option = mock(OpenOption.class);

        builder.addReadOption(option, true);

        assertThat(builder.supportedReadOptions).contains(option);
        assertThat(builder.defaultReadOptions).contains(option);
    }

    @Test
    public void addReadOptionFailRegisteredAsWriteOption()
    {
        final StandardOpenOption option = StandardOpenOption.WRITE;

        try {
            builder.addReadOption(option, false);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactoryBuilder.IS_WRITE_OPTION, option
            ));
        }
    }

    @Test
    public void addWriteOptionNoDefaultTest()
    {
        final OpenOption option = mock(OpenOption.class);

        builder.addWriteOption(option, false);

        assertThat(builder.supportedWriteOptions).contains(option);
        assertThat(builder.defaultWriteOptions).doesNotContain(option);
    }

    @Test
    public void addWriteOptionDefaultTest()
    {
        final OpenOption option = mock(OpenOption.class);

        builder.addWriteOption(option, true);

        assertThat(builder.supportedWriteOptions).contains(option);
        assertThat(builder.defaultWriteOptions).contains(option);
    }

    @Test
    public void addWriteOptionFailRegisteredAsReadOption()
    {
        final OpenOption option = StandardOpenOption.READ;

        try {
            builder.addWriteOption(option, false);
            shouldHaveThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage(String.format(
                OptionsFactoryBuilder.IS_READ_OPTION, option
            ));
        }
    }
}
