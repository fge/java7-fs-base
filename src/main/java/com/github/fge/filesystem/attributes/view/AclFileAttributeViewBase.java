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
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.List;

@SuppressWarnings("DesignForExtension")
public abstract class AclFileAttributeViewBase
    extends FileOwnerAttributeViewBase
    implements AclFileAttributeView
{
    /**
     * Returns the name of the attribute view. Attribute views of this type
     * have the name {@code "acl"}.
     */
    @Override
    public String name()
    {
        return "acl";
    }

    /**
     * Updates (replace) the access control list.
     * <p> Where the file system supports Access Control Lists, and it uses an
     * ACL model that differs from the NFSv4 defined ACL model, then this method
     * must translate the ACL to the model supported by the file system. This
     * method should reject (by throwing {@link IOException IOException}) any
     * attempt to write an ACL that would appear to make the file more secure
     * than would be the case if the ACL were updated. Where an implementation
     * does not support a mapping of {@link AclEntryType#AUDIT} or {@link
     * AclEntryType#ALARM} entries, then this method ignores these entries when
     * writing the ACL.
     * <p> If an ACL entry contains a {@link AclEntry#principal user-principal}
     * that is not associated with the same provider as this attribute view then
     * {@link ProviderMismatchException} is thrown. Additional validation, if
     * any, is implementation dependent.
     * <p> If the file system supports other security related file attributes
     * (such as a file {@link PosixFileAttributes#permissions
     * access-permissions} for example), the updating the access control list
     * may also cause these security related attributes to be updated.
     *
     * @param acl the new access control list
     * @throws IOException if an I/O error occurs or the ACL is invalid
     * @throws SecurityException In the case of the default provider, a
     * security manager is
     * installed, it denies {@link RuntimePermission}<tt>
     *     ("accessUserInformation")
     * </tt>
     * or its {@link SecurityManager#checkWrite(String) checkWrite}
     * method denies write access to the file.
     */
    @Override
    public void setAcl(final List<AclEntry> acl)
        throws IOException
    {
        throw new ReadOnlyAttributeException();
    }
}
