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

package com.github.fge.filesystem.driver.metadata.write;

import com.github.fge.filesystem.exceptions.ReadOnlyAttributeException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;

@ParametersAreNonnullByDefault
public final class BasicViewWriter
    extends ViewWriterFixedNames<BasicFileAttributeView>
{
    public BasicViewWriter(final BasicFileAttributeView view)
    {
        super(view, "lastModifiedTime", "lastAccessTime", "creationTime",
            "size", "isRegularFile", "isDirectory", "isSymbolicLink", "isOther",
            "fileKey");
    }

    @Override
    protected void doSetAttributeByName(final String name, final Object value)
        throws IOException
    {
        switch (name) {
            case "lastModifiedTime":
                view.setTimes((FileTime) value, null, null);
                break;
            case "lastAccessTime":
                view.setTimes(null, (FileTime) value, null);
                break;
            case "creationTime":
                view.setTimes(null, null, (FileTime) value);
                break;
            case "size":
            case "isRegularFile":
            case "isDirectory":
            case "isSymbolicLink":
            case "isOther":
            case "fileKey":
                throw new ReadOnlyAttributeException(name);
            default:
                throw new IllegalStateException();
        }
    }
}
