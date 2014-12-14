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
 * Core filesystem interface
 *
 * <h2>Design notes</h2>
 *
 * <p>This class is a consequence of a design decision: unlike what the original
 * API originally states, in this package it is believed that filesystems may
 * be completely unrelated to one another even if they share the same way of
 * dealing with I/O. One such case is online storage services, for instance.</p>
 *
 * <p>As such, this class takes care of all I/O operations that normally befall
 * to {@link java.nio.file.spi.FileSystemProvider}, which just delegates such
 * operations to driver instances according to the filesystem the paths are
 * issued from.</p>
 *
 * <h2>What this package is</h2>
 *
 * <p>Classes in this package are the main workhorse of your filesystem. This
 * is where all I/O is handled.</p>
 *
 * <p>All I/O methods of {@link java.nio.file.spi.FileSystemProvider}, and some
 * methods of {@link java.nio.file.FileSystem}, are handled by this class; one
 * driver will exist per filesystem.</p>
 *
 * <p>Although {@link com.github.fge.filesystem.driver.FileSystemDriver} is an
 * interface, you really want to extend {@link
 * com.github.fge.filesystem.driver.FileSystemDriverBase} instead, or even
 * {@link com.github.fge.filesystem.driver.UnixLikeFileSystemDriverBase}, since
 * Unix-like paths are by far the most common scenario.</p>
 */
package com.github.fge.filesystem.driver;