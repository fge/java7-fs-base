/*
* Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
*
* This software is dual-licensed under:
*
* - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
* later version;
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

package com.github.fge.filesystem.options;

import java.nio.file.CopyOption;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * File System Options
 * 
 * <p>This class allows you to register File System Options 
 * and add supported options.</p>
 *
 */

@ParametersAreNonnullByDefault
public class FileSystemOptionsFactory {
	
	private final Set<OpenOption> openOptions = new HashSet<>();
	private final Set<CopyOption> copyOptions = new HashSet<>();
	private final Set<LinkOption> linkOptions = new HashSet<>();
	
	public FileSystemOptionsFactory() {
		addCopyOption(StandardCopyOption.REPLACE_EXISTING);
		addOpenOption(StandardOpenOption.CREATE);
		addOpenOption(StandardOpenOption.CREATE_NEW);
		addOpenOption(StandardOpenOption.READ);
		addOpenOption(StandardOpenOption.SPARSE);
		addOpenOption(StandardOpenOption.TRUNCATE_EXISTING);
		addOpenOption(StandardOpenOption.WRITE);
	}
	
	/**
	 * <p>This method will take an OpenOption parameter and add it
	 * as OpenOption.</p>
	 * @param openOption OpenOption parameter
	 * @throws NullPointerException if openOption is null
	 */
	protected final void addOpenOption(final OpenOption openOption) {
		openOptions.add(Objects.requireNonNull(openOption));
	}
	
	/**
	 * <p>This method will take a CopyOption parameter and add it
	 * as CopyOption.</p>
	 * @param copyOption CopyOption parameter
	 * @throws NullPointerException if copyOption is null
	 */
	protected final void addCopyOption(final CopyOption copyOption) {
		copyOptions.add(Objects.requireNonNull(copyOption));
	}
	
	/**
	 * <p>This method will take a LinkOption parameter and add it
	 * as LinkOption.</p>
	 * @param linkOption LinkOption parameter
	 * @throws NullPointerException if linkOption is null
	 */
	protected final void addLinkOption(final LinkOption linkOption) {
		linkOptions.add(Objects.requireNonNull(linkOption));
	}

}
