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

import com.github.fge.filesystem.exceptions.IllegalOptionSetException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.fge.filesystem.CustomAssertions.assertThat;
import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.mockito.Mockito.mock;

public final class FileSystemOptionsFactoryTest
{
    private FileSystemOptionsFactory factory;

    @BeforeMethod
    public void init()
    {
        factory = new FileSystemOptionsFactory();
    }

    @DataProvider
    public Iterator<Object[]> compileReadOptionsValidData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] {
            new OpenOption[0],
            set(StandardOpenOption.READ)
        });
        list.add(new Object[] {
            array(StandardOpenOption.READ),
            set(StandardOpenOption.READ)
        });
        list.add(new Object[] {
            array(StandardOpenOption.SPARSE),
            set(StandardOpenOption.READ, StandardOpenOption.SPARSE)
        });

        return list.iterator();
    }

    @Test(dataProvider = "compileReadOptionsValidData")
    public void compileReadOptionsTest(final OpenOption[] opts,
        final Set<OpenOption> set)
    {
        final Set<OpenOption> actual = factory.compileReadOptions(opts);

        assertThat(actual).containsAll(set);
    }

    @DataProvider
    public Iterator<Object[]> compileReadOptionsInvalidData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { array(StandardOpenOption.WRITE) });
        list.add(new Object[] { array(StandardOpenOption.CREATE )});
        list.add(new Object[] { array(StandardOpenOption.CREATE_NEW )});

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "compileReadOptionsInvalidData")
    public void compileReadOptionsFailureTest(final OpenOption[] opts)
    {
        try {
            factory.compileReadOptions(opts);
            shouldHaveThrown(IllegalOptionSetException.class);
        } catch (IllegalOptionSetException e) {
            assertThat(e).hasMessage(Arrays.toString(opts));
        }
    }

    @Test
    public void addedReadOptionIsMarkedAsSupported()
    {
        final OpenOption myOption = mock(OpenOption.class);
        final FileSystemOptionsFactory myFactory
            = new FileSystemOptionsFactory()
        {
            {
                addReadOpenOption(myOption);
            }
        };

        final Set<OpenOption> set = myFactory.compileReadOptions(myOption);

        assertThat(set).containsOnly(myOption, StandardOpenOption.READ);
    }

    @DataProvider
    public Iterator<Object[]> compileWriteOptionsValidData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] {
            new OpenOption[0],
            set(StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        });
        list.add(new Object[] {
            array(StandardOpenOption.WRITE),
            set(StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        });
        list.add(new Object[] {
            array(StandardOpenOption.CREATE),
            set(StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        });
        list.add(new Object[] {
            array(StandardOpenOption.SPARSE),
            set(StandardOpenOption.WRITE, StandardOpenOption.CREATE,
                StandardOpenOption.SPARSE)
        });
        list.add(new Object[] {
            array(StandardOpenOption.TRUNCATE_EXISTING),
            set(StandardOpenOption.WRITE, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)
        });

        return list.iterator();
    }

    @Test(dataProvider = "compileWriteOptionsValidData")
    public void compileWriteOptionsTest(final OpenOption[] opts,
        final Set<OpenOption> set)
    {
        final Set<OpenOption> actual = factory.compileWriteOptions(opts);

        assertThat(actual).containsAll(set);
    }

    @DataProvider
    public Iterator<Object[]> compileWriteOptionsInvalidData()
    {
        final List<Object[]> list = new ArrayList<>();

        list.add(new Object[] { array(StandardOpenOption.READ) });

        return list.iterator();
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    @Test(dataProvider = "compileWriteOptionsInvalidData")
    public void compileWriteOptionsFailureTest(final OpenOption[] opts)
    {
        try {
            factory.compileWriteOptions(opts);
            shouldHaveThrown(IllegalOptionSetException.class);
        } catch (IllegalOptionSetException e) {
            assertThat(e).hasMessage(Arrays.toString(opts));
        }
    }

    @Test
    public void addedWriteOptionIsMarkedAsSupported()
    {
        final OpenOption myOption = mock(OpenOption.class);
        final FileSystemOptionsFactory myFactory
            = new FileSystemOptionsFactory()
        {
            {
                addWriteOpenOption(myOption);
            }
        };

        final Set<OpenOption> set = myFactory.compileWriteOptions(myOption);

        assertThat(set).containsOnly(myOption, StandardOpenOption.WRITE,
            StandardOpenOption.CREATE);
    }

    @Test
    public void addedOpenOptionIsSupportedForBothReadAndWrite()
    {
        final OpenOption myOption = mock(OpenOption.class);
        final FileSystemOptionsFactory myFactory
            = new FileSystemOptionsFactory()
        {
            {
                addOpenOption(myOption);
            }
        };

        final Set<OpenOption> readSet = myFactory.compileReadOptions(myOption);
        final Set<OpenOption> writeSet
            = myFactory.compileWriteOptions(myOption);

        assertThat(readSet).containsOnly(StandardOpenOption.READ, myOption);
        assertThat(writeSet).containsOnly(StandardOpenOption.WRITE,
            StandardOpenOption.CREATE, myOption);
    }

    @SafeVarargs
    private static <T> T[] array(final T... elements)
    {
        return elements;
    }

    @SafeVarargs
    private static <T> Set<T> set(final T... elements)
    {
        final Set<T> set = new HashSet<>();

        for (final T element: elements)
            set.add(element);

        return set;
    }
}
