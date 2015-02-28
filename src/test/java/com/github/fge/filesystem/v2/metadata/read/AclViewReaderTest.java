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
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import static com.github.fge.filesystem.CustomAssertions.shouldHaveThrown;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AclViewReaderTest
    extends ViewReaderTest<AclViewReader, AclFileAttributeView>
{
    public AclViewReaderTest()
    {
        super(AclFileAttributeView.class, "acl", "owner");
    }

    @Override
    protected void initReader()
    {
        reader = new AclViewReader(view);
    }

    @Test
    public void readAclTest()
        throws IOException
    {
        reader.getAttributeByName("acl");

        verify(view, only()).getAcl();
    }

    @Test(dependsOnMethods = "readAclTest")
    public void readAclSuccessTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<AclEntry> acl = mock(List.class);

        when(view.getAcl()).thenReturn(acl);

        final Object actual = reader.getAttributeByName("acl");

        assertThat(actual).isSameAs(acl);
    }

    @Test(dependsOnMethods = "readAclTest")
    public void readAclFailureTest()
        throws IOException
    {
        final IOException exception = new IOException();

        when(view.getAcl()).thenThrow(exception);

        try {
            reader.getAttributeByName("acl");
            shouldHaveThrown(IOException.class);
        } catch (IOException e) {
            assertThat(e).isSameAs(exception);
        }
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
