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
 * File attribute view providers
 *
 * <p>Classes in this package wrap a file attribute view class and (if any) a
 * file attributes class into a single class, and offers additional features.
 * </p>
 *
 * <p>The most notable feature added to this class is the ability to get and
 * set attributes by name; the JDK offers no helper classes for this!</p>
 *
 * <p>Also, for some attribute views, default values are provided which you can
 * rely upon. An example is that all file times in {@link
 * com.github.fge.filesystem.attributes.provider.BasicFileAttributesProvider}
 * return the Unix epoch (that is, Jan 1st, 1970 at 00:00:00 GMT) by default
 * (this is what should be returned if the underlying object has no support for
 * file times).</p>
 *
 * <p>There are base implementations for all attribute views defined by the JDK.
 * You can provide your own by implementing {@link
 * com.github.fge.filesystem.attributes.provider.FileAttributesProvider} (in
 * which case you should not forget to implement a {@link
 * com.github.fge.filesystem.attributes.descriptor.AttributesDescriptor
 * descriptor} for your new view as well).</p>
 *
 * <p>Note also that you <strong>must</strong> override write methods; by
 * default, implementations report that writing an attribute is not supported
 * by throwning a {@link
 * com.github.fge.filesystem.exceptions.ReadOnlyAttributeException}.</p>
 *
 * @see java.nio.file.attribute
 * @see com.github.fge.filesystem.attributes.FileAttributesFactory
 */
package com.github.fge.filesystem.attributes.provider;