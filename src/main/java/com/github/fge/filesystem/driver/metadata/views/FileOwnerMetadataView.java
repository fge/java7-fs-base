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

package com.github.fge.filesystem.driver.metadata.views;

import com.github.fge.filesystem.driver.metadata.MetadataDriver;
import com.github.fge.filesystem.driver.metadata.readers.FileOwnerAttributeReader;
import com.github.fge.filesystem.driver.metadata.writers.FileOwnerAttributeWriter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;

@ParametersAreNonnullByDefault
public class FileOwnerMetadataView<M>
    extends MetadataViewNoAttributes<M, FileOwnerAttributeReader<M>, FileOwnerAttributeWriter<M>>
    implements FileOwnerAttributeView
{
    public FileOwnerMetadataView(final Path path,
        final MetadataDriver<M> driver)
    {
        super("owner", path, driver);
    }

    @Override
    public final UserPrincipal getOwner()
        throws IOException
    {
        return reader.getOwner();
    }

    @Override
    public final void setOwner(final UserPrincipal owner)
        throws IOException
    {
        writer.setOwner(owner);
    }
}
