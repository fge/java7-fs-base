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

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

@ParametersAreNonnullByDefault
public final class FileOwnerViewWriter
    extends ViewWriterFixedNames<FileOwnerAttributeView>
{
    public FileOwnerViewWriter(final FileOwnerAttributeView view)
    {
        super(view, "owner");
    }

    @Override
    protected void doSetAttributeByName(final String name, final Object value)
        throws IOException
    {
        view.setOwner((UserPrincipal) value);
    }
}
