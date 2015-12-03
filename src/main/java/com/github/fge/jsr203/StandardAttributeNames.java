package com.github.fge.jsr203;

import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

public final class StandardAttributeNames
{

    private StandardAttributeNames()
    {
        throw new Error("instantiation not permitted");
    }

    /**
     * Owner attribute
     *
     * <p>Available from {@link FileOwnerAttributeView} and, by extension,
     * {@link AclFileAttributeView} and {@link PosixFileAttributeView}.</p>
     *
     * @see FileOwnerAttributeView#getOwner()
     * @see FileOwnerAttributeView#setOwner(UserPrincipal)
     */
    public static final String OWNER = "owner";

    /**
     * ACL attribute
     *
     * @see AclFileAttributeView#getAcl()
     * @see AclFileAttributeView#setAcl(List)
     */
    public static final String ACL = "acl";
}
