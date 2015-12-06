package com.github.fge.jsr203.attrs.constants;

import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;
import java.util.Set;

public final class StandardAttributeNames
{

    private StandardAttributeNames()
    {
        throw new Error("instantiation not permitted");
    }

    /**
     * Last modified time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#lastModifiedTime()
     * @see BasicFileAttributeView#setTimes(FileTime, FileTime, FileTime)
     */
    public static final String LAST_MODIFIED_TIME = "lastModifiedTime";

    /**
     * Last access time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#lastAccessTime()
     * @see BasicFileAttributeView#setTimes(FileTime, FileTime, FileTime)
     */
    public static final String LAST_ACCESS_TIME = "lastAccessTime";

    /**
     * Creation time
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#creationTime()
     * @see BasicFileAttributeView#setTimes(FileTime, FileTime, FileTime)
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
     *
     * <p>Available from {@link BasicFileAttributes} and, by extension, from
     * {@link DosFileAttributes} and {@link PosixFileAttributes}.</p>
     *
     * @see BasicFileAttributes#fileKey()
     */
    public static final String FILE_KEY = "fileKey";

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
     * Group attribute
     *
     * @see PosixFileAttributes#group()
     * @see PosixFileAttributeView#setGroup(GroupPrincipal)
     */
    public static final String GROUP = "group";

    /**
     * POSIX permissions
     *
     * @see PosixFileAttributes#permissions()
     * @see PosixFileAttributeView#setPermissions(Set)
     */
    public static final String PERMISSIONS = "permissions";

    /**
     * ACL attribute
     *
     * @see AclFileAttributeView#getAcl()
     * @see AclFileAttributeView#setAcl(List)
     */
    public static final String ACL = "acl";

    /**
     * DOS read only attribute
     *
     * @see DosFileAttributes#isReadOnly()
     * @see DosFileAttributeView#setReadOnly(boolean)
     */
    public static final String READONLY = "readonly";

    /**
     * DOS hidden attribute
     *
     * @see DosFileAttributes#isHidden()
     * @see DosFileAttributeView#setHidden(boolean)
     */
    public static final String HIDDEN = "hidden";

    /**
     * DOS system attribute
     *
     * @see DosFileAttributes#isSystem()
     * @see DosFileAttributeView#setSystem(boolean)
     */
    public static final String SYSTEM = "system";

    /**
     * DOS archive attribute
     *
     * @see DosFileAttributes#isArchive()
     * @see DosFileAttributeView#setArchive(boolean)
     */
    public static final String ARCHIVE = "archive";

    /**
     * Special case for all attributes
     *
     * <p>This happens if you invoke {@link
     * FileSystemProvider#readAttributes(Path, String, LinkOption...)} with the
     * string specification containing {@code xxx:*} as the attribute name (
     * which means all attributes for the view identified by {@code xxx}).</p>
     */
    public static final String ALL = "*";
}
