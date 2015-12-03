package com.github.fge.jsr203;

import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
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

    /**
     * Last modified time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#lastModifiedTime()
     */
    public static final String LAST_MODIFIED_TIME = "lastModifiedTime";

    /**
     * Last access time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#lastAccessTime()
     */
    public static final String LAST_ACCESS_TIME = "lastAccessTime";

    /**
     * Creation time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#creationTime()
     */
    public static final String CREATION_TIME = "creationTime";

    /**
     * File size
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#size()
     */
    public static final String SIZE = "size";

    /**
     * Is the path a regular file?
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#isRegularFile()
     */
    public static final String IS_REGULAR_FILE = "isRegularFile";

    /**
     * Is the path a directory?
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#isDirectory()
     */
    public static final String IS_DIRECTORY = "isDirectory";

    /**
     * Is the path a symbolic link?
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#isSymbolicLink()
     */
    public static final String IS_SYMBOLIC_LINK = "isSymbolicLink";

    /**
     * Is the path another type of file (ie, neither a regular file, a directory
     * or a symbolic link)?
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#isOther()
     */
    public static final String IS_OTHER = "isOther";

    /**
     * Get the file key for a path
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#fileKey()
     */
    public static final String FILE_KEY = "fileKey";
}
