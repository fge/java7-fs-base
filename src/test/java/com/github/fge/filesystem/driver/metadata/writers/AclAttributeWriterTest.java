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

package com.github.fge.filesystem.driver.metadata.writers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class AclAttributeWriterTest
    extends AttributeWriterTest<AclAttributeWriter<Object>>
{
    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        writer = spy(new AclAttributeWriter<Object>(path, driver)
        {
        });

        doNothing().when(writer).setOwner(any(UserPrincipal.class));
        //noinspection unchecked
        doNothing().when(writer).setAcl(anyList());
    }

    @Test
    public void setOwnerTest()
        throws IOException
    {
        final UserPrincipal owner = mock(UserPrincipal.class);

        writer.setAttributeByName("owner", owner);

        verify(writer).setOwner(same(owner));
    }

    @Test
    public void setAclTest()
        throws IOException
    {
        @SuppressWarnings("unchecked")
        final List<AclEntry> acl = mock(List.class);

        writer.setAttributeByName("acl", acl);

        verify(writer).setAcl(same(acl));
    }
}
