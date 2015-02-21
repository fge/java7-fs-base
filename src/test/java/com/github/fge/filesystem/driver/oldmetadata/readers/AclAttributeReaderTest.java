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

package com.github.fge.filesystem.driver.oldmetadata.readers;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class AclAttributeReaderTest
    extends AttributeReaderTest<AclAttributeReader<Object>>
{
    @BeforeMethod
    @Override
    public void init()
        throws IOException
    {
        reader = spy(new AclAttributeReader<Object>(path, driver)
        {
            @Override
            public UserPrincipal getOwner()
            {
                return null;
            }

            @Override
            public List<AclEntry> getAcl()
                throws IOException
            {
                return null;
            }
        });

        final UserPrincipal owner = mock(UserPrincipal.class);
        values.put("owner", owner);
        doReturn(owner).when(reader).getOwner();

        @SuppressWarnings("unchecked")
        final List<AclEntry> acl = mock(List.class);
        values.put("acl", acl);
        doReturn(acl).when(reader).getAcl();
    }

    @Test
    public void getOwnerTest()
        throws IOException
    {
        final Object byName = reader.getAttributeByName("owner");

        final Object byMethod = values.get("owner");

        verify(reader).getOwner();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void getAclTest()
        throws IOException
    {
        final Object byName = reader.getAttributeByName("acl");

        final Object byMethod = values.get("acl");

        verify(reader).getAcl();

        assertThat(byName).isSameAs(byMethod);
    }
}
