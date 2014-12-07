/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
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

package com.github.fge.filesystem.attributes.view;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;

@SuppressWarnings("DesignForExtension")
public abstract class PosixFileAttributeViewBase
    extends BasicFileAttributeViewBase
    implements PosixFileAttributeView
{
    /**
     * Returns the name of the attribute view. Attribute views of this type
     * have the name {@code "posix"}.
     */
    @Override
    public String name()
    {
        return "posix";
    }

    /**
     * Updates the file owner.
     * <p> It it implementation specific if the file owner can be a {@link
     * GroupPrincipal group}. To ensure consistent and correct behavior
     * across platforms it is recommended that this method should only be used
     * to set the file owner to a user principal that is not a group.
     *
     * @param owner the new file owner
     * @throws IOException if an I/O error occurs, or the {@code owner}
     * parameter is a
     * group and this implementation does not support setting the owner
     * to a group
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, and it denies {@link
     * RuntimePermission}<tt>("accessUserInformation")</tt> or its
     * {@link SecurityManager#checkWrite(String) checkWrite} method
     * denies write access to the file.
     */
    @Override
    public void setOwner(final UserPrincipal owner)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    /**
     * Updates the file permissions.
     *
     * @param perms the new set of permissions
     * @throws ClassCastException if the sets contains elements that are not
     * of type {@code
     * PosixFilePermission}
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, and it denies {@link RuntimePermission}<tt>
     *     ("accessUserInformation")
     * </tt>
     * or its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file.
     */
    @Override
    public void setPermissions(final Set<PosixFilePermission> perms)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }

    /**
     * Updates the file group-owner.
     *
     * @param group the new file group-owner
     * @throws IOException if an I/O error occurs
     * @throws SecurityException In the case of the default provider, and a
     * security manager is
     * installed, it denies {@link RuntimePermission}<tt>
     *     ("accessUserInformation")
     * </tt>
     * or its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file.
     */
    @Override
    public void setGroup(final GroupPrincipal group)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }
}
