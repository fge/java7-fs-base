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

package com.github.fge.filesystem.v2.metadata.write;

import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

public final class FileOwnerViewWriterTest
    extends ViewWriterTest<FileOwnerViewWriter, FileOwnerAttributeView>
{
    public FileOwnerViewWriterTest()
    {
        super(FileOwnerAttributeView.class);
    }

    @Override
    protected void initWriter()
        throws IOException
    {
        writer = new FileOwnerViewWriter(view);
    }

    @Test
    public void setOwnerSuccessTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        writer.setAttributeByName("owner", owner);

        verify(view, only()).setOwner(same(owner));
    }

    @Test
    public void setOwnerFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        doThrow(exception).when(view).setOwner(any(UserPrincipal.class));

        try {
            writer.setAttributeByName("owner", mock(UserPrincipal.class));
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
    }
}
