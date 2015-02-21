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

package com.github.fge.filesystem.driver.oldmetadata.attributes;

import com.github.fge.filesystem.driver.oldmetadata.PathMetadata;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public final class PosixAttributesTest
    extends AttributesTest<PosixAttributes<Object>>
{
    @Override
    @SuppressWarnings({ "OverlyLongMethod", "ConstantConditions" })
    @BeforeMethod
    public void init()
    {
        values.clear();

        //noinspection unchecked
        pathMetadata = mock(PathMetadata.class);
        //noinspection EmptyClass
        attributes = spy(new PosixAttributes<Object>(pathMetadata)
        {
            @Override
            public UserPrincipal owner()
            {
                return null;
            }

            @Override
            public GroupPrincipal group()
            {
                return null;
            }

            @Override
            public Set<PosixFilePermission> permissions()
            {
                return null;
            }
        });

        String name;
        FileTime fileTime;
        Boolean bool;
        final Long size;
        final Object fileKey;
        final Set<PosixFilePermission> permissions;
        final UserPrincipal userPrincipal;
        final GroupPrincipal groupPrincipal;

        name = "lastModifiedTime";
        fileTime = FileTime.fromMillis(0L);
        values.put(name, fileTime);
        doReturn(fileTime).when(attributes).lastModifiedTime();

        name = "lastAccessTime";
        fileTime = FileTime.fromMillis(0L);
        values.put(name, fileTime);
        doReturn(fileTime).when(attributes).lastAccessTime();

        name = "creationTime";
        fileTime = FileTime.fromMillis(0L);
        values.put(name, fileTime);
        doReturn(fileTime).when(attributes).creationTime();

        name = "size";
        size = 29098L;
        values.put(name, size);
        doReturn(size).when(attributes).size();

        name = "isRegularFile";
        bool = true;
        values.put(name, bool);
        doReturn(bool).when(attributes).isRegularFile();

        name = "isDirectory";
        bool = false;
        values.put(name, bool);
        doReturn(bool).when(attributes).isDirectory();

        name = "isSymbolicLink";
        bool = false;
        values.put(name, bool);
        doReturn(bool).when(attributes).isSymbolicLink();

        name = "isOther";
        bool = false;
        values.put(name, bool);
        doReturn(bool).when(attributes).isOther();

        name = "fileKey";
        fileKey = new Object();
        values.put(name, fileKey);
        doReturn(fileKey).when(attributes).fileKey();

        name = "owner";
        userPrincipal = mock(UserPrincipal.class);
        values.put(name, userPrincipal);
        doReturn(userPrincipal).when(attributes).owner();

        name = "group";
        groupPrincipal = mock(GroupPrincipal.class);
        values.put(name, groupPrincipal);
        doReturn(groupPrincipal).when(attributes).group();

        name = "permissions";
        //noinspection unchecked
        permissions = mock(Set.class);
        values.put(name, permissions);
        doReturn(permissions).when(attributes).permissions();
    }

    @Test
    public void lastModifiedTimeTest()
    {
        final Object byName
            = attributes.getAttributeByName("lastModifiedTime");

        final Object byMethod = values.get("lastModifiedTime");

        verify(attributes).lastModifiedTime();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void lastAccessTimeTest()
    {
        final Object byName
            = attributes.getAttributeByName("lastAccessTime");

        final Object byMethod = values.get("lastAccessTime");

        verify(attributes).lastAccessTime();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void creationTimeTest()
    {
        final Object byName = attributes.getAttributeByName("creationTime");

        final Object byMethod = values.get("creationTime");

        verify(attributes).creationTime();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void sizeTest()
    {
        final Object byName = attributes.getAttributeByName("size");

        final Object byMethod = values.get("size");

        verify(attributes).size();

        // Boxing and unboxing here; therefore we cannot use .isSameAs()...
        assertThat(byName).isEqualTo(byMethod);
    }

    @Test
    public void isRegularFileTest()
    {
        final Object byName = attributes.getAttributeByName("isRegularFile");

        final Object byMethod = values.get("isRegularFile");

        verify(attributes).isRegularFile();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void isDirectoryTest()
    {
        final Object byName = attributes.getAttributeByName("isDirectory");

        final Object byMethod = values.get("isDirectory");

        verify(attributes).isDirectory();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void isSymbolicLinkTest()
    {
        final Object byName = attributes.getAttributeByName("isSymbolicLink");

        final Object byMethod = values.get("isSymbolicLink");

        verify(attributes).isSymbolicLink();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void isOtherTest()
    {
        final Object byName = attributes.getAttributeByName("isOther");

        final Object byMethod = values.get("isOther");

        verify(attributes).isOther();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void ownerTest()
    {
        final Object byName = attributes.getAttributeByName("owner");

        final Object byMethod = values.get("owner");

        verify(attributes).owner();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void groupTest()
    {
        final Object byName = attributes.getAttributeByName("group");

        final Object byMethod = values.get("group");

        verify(attributes).group();

        assertThat(byName).isSameAs(byMethod);
    }

    @Test
    public void permissionsTest()
    {
        final Object byName = attributes.getAttributeByName("permissions");

        final Object byMethod = values.get("permissions");

        verify(attributes).permissions();

        assertThat(byName).isSameAs(byMethod);
    }
}
