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
 * File attributes and attribute views
 *
 * <h2>Warning: attribute loading</h2>
 *
 * <p>Right now, there is a bug with the way classes in this package implement
 * attribute views; they are supposedly only loaded when you need them ({@link
 * java.nio.file.Files#getFileAttributeView(java.nio.file.Path, java.lang.Class,
 * java.nio.file.LinkOption...)} does not throw an exception), however this
 * package loads them eagerly. At this moment, if attributes fail to load, an
 * {@link com.github.fge.filesystem.exceptions.UncaughtIOException}
 * (unchecked)
 * is thrown.</p>
 *
 * <p>This will be fixed in a future release.</p>
 *
 * <h2>What this package does</h2>
 *
 * <p>This package provides everything you need to implement attributes; not
 * only the ones defined by the JDK, but also your own. Attribute dispatch
 * (creation, string handling etc) is already done for you in {@link
 * com.github.fge.filesystem.driver.FileSystemDriverBase}; you only have to
 * implement attributes and provide a {@link
 * com.github.fge.filesystem.attributes.FileAttributesFactory}.</p>
 *
 * <h2>Implementing attributes</h2>
 *
 * <p>This package wraps all attribute views in {@link
 * com.github.fge.filesystem.attributes.provider provider classes}. After you
 * implement the attribute providers you need, you will need to extend {@link
 * com.github.fge.filesystem.attributes.FileAttributesFactory} and register your
 * attribute providers. For instance:</p>
 *
 * <pre>
 *     public final class MyFileAttributesProvider
 *         extends FileAttributesProvider
 *     {
 *         public MyFileAttributesProvider()
 *         {
 *             // You MUST do this first
 *             setMetadataClass(MyMetadataClass.class);
 *             addImplementation("basic", MyBasicFileAttributesProvider.class);
 *         }
 *     }
 * </pre>
 *
 * <p>Reminder: the API requires that {@code basic} attributes be implemented by
 * all filesystem implementations.</p>
 *
 * <h2>Implementing custom attributes</h2>
 *
 * <p>You are not limited to the basic types provided by the JDK; you can also
 * provide your own attributes. For this, you should first have an
 * implementation for your attributes (either by extending an existing attribute
 * provider class or by directly extending {@link
 * com.github.fge.filesystem.attributes.provider.FileAttributesProvider}),
 * choose a name for your attribute view and create an {@link
 * com.github.fge.filesystem.attributes.descriptor.AttributesDescriptor} for it.
 * You will then register it to the factory -- do this <em>before</em>
 * registering an implementation. The default factory already includes all
 * descriptors for attributes defined by the JDK.</p>
 *
 * <p>Example:</p>
 *
 * <pre>
 *     public final class MyFileAttributesProvider
 *         extends FileAttributesProvider
 *     {
 *         public MyFileAttributesProvider()
 *         {
 *             // You MUST do this first
 *             setMetadataClass(MyMetadataClass.class);
 *             addDescriptor(new MyCoolAttributesDescriptor());
 *             // Provided that the name returned by the descriptor is "cool"
 *             addImplementation("cool", MyCoolFileAttributesProvider.class);
 *         }
 *     }
 * </pre>
 *
 * <p>Note that this is an error to try and register a descriptor with the same
 * name twice.</p>
 */

package com.github.fge.filesystem.attributes;
