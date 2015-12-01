package com.github.fge.jsr203.attrs;

/**
 * List of standard attribute view names defined by the JDK
 */
public final class StandardAttributeViewNames
{
    private StandardAttributeViewNames()
    {
        throw new Error("instantiation not permitted");
    }

    public static final String ACL = "acl";
    public static final String BASIC = "basic";
    public static final String DOS = "dos";
    public static final String OWNER = "owner";
    public static final String POSIX = "posix";
    public static final String USER = "user";
}
