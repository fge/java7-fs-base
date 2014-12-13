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

/**
 * Descriptors for file attributes
 *
 * <h2>Implementation note</h2>
 *
 * <p>As of today, file attributes are supported; however, filestore
 * attributes are <strong>not</strong>.</p>
 *
 * <h2>What this package does</h2>
 *
 * <p>The java.nio.file API defines two types of file attribute classes: views
 * and attributes. A view is defined by its name and an associated interface
 * (for instance, the {@code "posix"} view is associated with {@link
 * java.nio.file.attribute.PosixFileAttributeView}); some views, but not all,
 * also have an associated attributes class ({@link
 * java.nio.file.attribute.PosixFileAttributes} for {@code "posix"}; {@code
 * "owner"} is an example of a view with no associated attributes class).</p>
 *
 * <p>This package simply provides a wrapper to describe such relationships. All
 * views currently defined by the JDK are wrapped in enumeration {@link
 * com.github.fge.filesystem.attributes.descriptor.StandardAttributesDescriptor}.
 * </p>
 *
 * <p>You can create your own if you so desire; beware, however, not to reuse
 * one defined by the JDK! Those are:</p>
 *
 * <ul>
 *     <li>"acl";</li>
 *     <li>"basic";</li>
 *     <li>"dos" (yes, in 2014 -- don't ask me why);</li>
 *     <li>"owner";</li>
 *     <li>"posix";</li>
 *     <li>"user".</li>
 * </ul>
 *
 * <p>All views (except for {@code "user"} also define the set of attribute
 * names they support; however, this class does not have this information. This
 * is left to the {@link com.github.fge.filesystem.attributes.provider
 * attribute providers} to deal with.</p>
 *
 * @see java.nio.file.attribute
 */


package com.github.fge.filesystem.attributes.descriptor;

