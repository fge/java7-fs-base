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

package com.github.fge.filesystem.attributes.descriptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttributeView;

/**
 * One file attribute descriptor
 */
public interface AttributesDescriptor
{
    /**
     * Get the name of the attribute view
     *
     * @return the name
     */
    @Nonnull
    String getName();

    /**
     * Get the file attribute view class associated with this view
     *
     * @return the view class
     */
    @Nonnull
    Class<? extends FileAttributeView> getViewClass();

    /**
     * Get the file attributes class (if any) associated with this view
     *
     * @return the attributes class, or {@code null} if this view does not
     * have an attributes class
     */
    @Nullable
    Class<? extends BasicFileAttributes> getAttributeClass();
}
