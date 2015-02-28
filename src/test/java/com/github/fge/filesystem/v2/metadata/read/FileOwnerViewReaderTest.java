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

package com.github.fge.filesystem.v2.metadata.read;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class FileOwnerViewReaderTest
    extends ViewReaderTest<FileOwnerViewReader, FileOwnerAttributeView>
{
    public FileOwnerViewReaderTest()
    {
        super(FileOwnerAttributeView.class, "owner");
    }

    @Override
    protected void initReader()
    {
        reader = new FileOwnerViewReader(view);
    }

    @Test
    public void readOwnerTest()
        throws IOException
    {
        reader.getAttributeByName("owner");

        verify(view, only()).getOwner();
    }

    @Test(dependsOnMethods = "readOwnerTest")
    public void readOwnerSuccessTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        when(view.getOwner()).thenReturn(owner);

        final Object actual = reader.getAttributeByName("owner");

        assertThat(actual).isSameAs(owner);
    }

    @Test(dependsOnMethods = "readOwnerTest")
    public void readOwnerFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(view.getOwner()).thenThrow(exception);

        try {
            reader.getAttributeByName("owner");
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }
}
