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

import com.github.fge.filesystem.exceptions.IllegalOptionSetException;
import com.github.fge.filesystem.exceptions.UnsupportedOptionException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.CopyOption;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Repository of options supported by a filesystem
 *
 * <p>The default implementation supposes support for the following options:</p>
 *
 * <ul>
 *     <li>{@link StandardOpenOption#CREATE};</li>
 *     <li>{@link StandardOpenOption#CREATE_NEW};</li>
 *     <li>{@link StandardOpenOption#READ};</li>
 *     <li>{@link StandardOpenOption#SPARSE};</li>
 *     <li>{@link StandardOpenOption#TRUNCATE_EXISTING};</li>
 *     <li>{@link StandardOpenOption#WRITE};</li>
 *     <li>{@link StandardCopyOption#REPLACE_EXISTING}.</li>
 * </ul>
 *
 * <p>Extend this class if you want to add support for further options. For
 * instance, if atomic move is supported:</p>
 *
 * <pre>
 *     public final class MyFileSystemOptionsRepository
 *         extends FileSystemOptionsRepository
 *     {
 *         public MyFileSystemOptionsRepository()
 *         {
 *             addCopyOption(StandardCopyOption.ATOMIC_MOVE);
 *         }
 *     }
 * </pre>
 *
 * <p>Unless otherwise noted, all methods in this class will throw a {@link
 * NullPointerException} if a null argument is passed.</p>
 */
// TODO: see newByteChannel() and its Set<? extends OpenOption>
@ParametersAreNonnullByDefault
public class FileSystemOptionsFactory
{
	private final Set<OpenOption> readOpenOptions = new HashSet<>();
	private final Set<OpenOption> writeOpenOptions = new HashSet<>();
	private final Set<CopyOption> copyOptions = new HashSet<>();
	private final Set<LinkOption> linkOptions
		= EnumSet.noneOf(LinkOption.class);
	private final Map<CopyOption, Set<OpenOption>> readTranslations
		= new HashMap<>();
	private final Map<CopyOption, Set<OpenOption>> writeTranslations
		= new HashMap<>();

	public FileSystemOptionsFactory()
	{
		addCopyOption(StandardCopyOption.REPLACE_EXISTING);

		// Note: Javadoc says option should be ignored if not supported, so...
		addOpenOption(StandardOpenOption.SPARSE);

		addReadOpenOption(StandardOpenOption.READ);

		addWriteOpenOption(StandardOpenOption.CREATE);
		addWriteOpenOption(StandardOpenOption.CREATE_NEW);
		addWriteOpenOption(StandardOpenOption.TRUNCATE_EXISTING);
		addWriteOpenOption(StandardOpenOption.WRITE);

		addWriteTranslation(StandardCopyOption.REPLACE_EXISTING,
			StandardOpenOption.CREATE);
		addWriteTranslation(StandardCopyOption.REPLACE_EXISTING,
			StandardOpenOption.TRUNCATE_EXISTING);
		addWriteTranslation(StandardCopyOption.REPLACE_EXISTING,
			StandardOpenOption.WRITE);

		// TODO: for now, cannot add translations for unsupported options
	}

	/**
	 * Compile a set of read options from a given {@link OpenOption} array
	 *
	 * <p>The result set will have at least {@link StandardOpenOption#READ} if
	 * it is not already present.</p>
	 *
	 * @param opts the options array
	 * @return an unmodifiable set of read options
	 * @throws UnsupportedOptionException one or more options are not supported
	 * @throws IllegalOptionSetException some options are unsuited for read
	 */
	@Nonnull
	public final Set<OpenOption> compileReadOptions(final OpenOption... opts)
	{
		final Set<OpenOption> set = new HashSet<>();

		for (final OpenOption opt: opts)
			set.add(Objects.requireNonNull(opt));

		if (set.removeAll(writeOpenOptions))
			throw new IllegalOptionSetException(Arrays.toString(opts));

		for (final OpenOption opt: set)
			if (!readOpenOptions.contains(opt))
				throw new UnsupportedOptionException(opt.toString());

		// We want at least READ
		set.add(StandardOpenOption.READ);

		return Collections.unmodifiableSet(set);
	}

	/**
	 * Compile a set of read options from a given {@link OpenOption} array
	 *
	 * <p>The result set will have at least {@link StandardOpenOption#WRITE} if
	 * it is not already present.</p>
	 *
	 * @param opts the options array
	 * @return an unmodifiable set of write options
	 * @throws UnsupportedOptionException one or more options are not supported
	 * @throws IllegalOptionSetException some options are unsuited for write
	 */
	@Nonnull
	public final Set<OpenOption> compileWriteOptions(final OpenOption... opts)
	{
		final Set<OpenOption> set = new HashSet<>();

		for (final OpenOption opt: opts)
			set.add(Objects.requireNonNull(opt));

		if (set.removeAll(readOpenOptions))
			throw new IllegalOptionSetException(Arrays.toString(opts));

		for (final OpenOption opt: opts) {
			if (!writeOpenOptions.contains(Objects.requireNonNull(opt)))
				throw new UnsupportedOptionException(opt.toString());
			set.add(opt);
		}

		// We substitute ourselves for FileSystemProvider here, so we need
		// to set options which are present if none are provided
		set.add(StandardOpenOption.WRITE);
		if (!set.contains(StandardOpenOption.APPEND))
			set.add(StandardOpenOption.TRUNCATE_EXISTING);
		if (!set.contains(StandardOpenOption.CREATE_NEW))
			set.add(StandardOpenOption.CREATE);


		return Collections.unmodifiableSet(set);
	}

	/**
	 * Compile a set of copy options from a {@link CopyOption} array
	 *
	 * @param opts the options array
	 * @return an unmodifiable set of options
	 */
	@Nonnull
	public final Set<CopyOption> compileCopyOptions(final CopyOption... opts)
	{
		final Set<CopyOption> set = new HashSet<>();
		for (final CopyOption opt: opts) {
			if (!copyOptions.contains(Objects.requireNonNull(opt)))
				throw new UnsupportedOptionException(opt.toString());
			set.add(opt);
		}

		return Collections.unmodifiableSet(set);
	}

	@Nonnull
	public final Set<LinkOption> compileLinkOptions(final LinkOption... opts)
	{
		final Set<LinkOption> set = EnumSet.noneOf(LinkOption.class);
		for (final LinkOption opt: opts) {
			if (!linkOptions.contains(Objects.requireNonNull(opt)))
				throw new UnsupportedOptionException(opt.toString());
			set.add(opt);
		}

		return Collections.unmodifiableSet(set);
	}

	@Nonnull
	public final Set<OpenOption> toReadOptions(final Set<CopyOption> options)
	{
		final Set<OpenOption> set = new HashSet<>();

		for (final CopyOption option: options) {
			if (!copyOptions.contains(Objects.requireNonNull(option)))
				throw new UnsupportedOptionException(option.toString());
			if (readTranslations.containsKey(option))
				set.addAll(readTranslations.get(option));
		}

		set.add(StandardOpenOption.READ);

		return Collections.unmodifiableSet(set);
	}

	@Nonnull
	public final Set<OpenOption> toWriteOptions(final Set<CopyOption> options)
	{
		return copyTranslations(options, writeTranslations);
	}

	/**
	 * Add an open option supported for read
	 *
	 * @param option the option
	 */
	protected final void addReadOpenOption(final OpenOption option)
	{
		readOpenOptions.add(Objects.requireNonNull(option));
	}

	/**
	 * Add an option option supported for write
	 *
	 * @param option the option
	 */
	protected final void addWriteOpenOption(final OpenOption option)
	{
		writeOpenOptions.add(Objects.requireNonNull(option));
	}

	/**
	 * Add an option option supported for both read and write
	 *
	 * @param option the option
	 */
	protected final void addOpenOption(final OpenOption option)
	{
		addReadOpenOption(option);
		addWriteOpenOption(option);
	}

	/**
	 * Add a supported copy option
	 *
	 * @param option the option
	 */
	protected final void addCopyOption(final CopyOption option)
	{
		copyOptions.add(Objects.requireNonNull(option));
	}

	/**
	 * Add a supported link option
	 *
	 * @param option the option
	 */
	protected final void addLinkOption(final LinkOption option)
	{
		linkOptions.add(Objects.requireNonNull(option));
		addOpenOption(option);
		copyOptions.add(option);
	}

	protected final void addReadTranslation(final CopyOption option,
		final OpenOption translated)
	{
		if (!copyOptions.contains(Objects.requireNonNull(option)))
			throw new IllegalArgumentException("option " + option + " is not "
				+ "a supported copy option (did you forget to .addCopyOption"
				+ "()?)");
		if (!readOpenOptions.contains(Objects.requireNonNull(translated)))
			throw new IllegalArgumentException("option " + translated + "is "
				+ "not a supported read option (did you forget to "
				+ ".addReadOpenOption()?)");
		if (!readTranslations.containsKey(option))
			readTranslations.put(option, new HashSet<OpenOption>());
		readTranslations.get(option).add(translated);
	}

	protected final void addWriteTranslation(final CopyOption option,
		final OpenOption translated)
	{
		if (!copyOptions.contains(Objects.requireNonNull(option)))
			throw new IllegalArgumentException("option " + option + " is not "
				+ "a supported copy option (did you forget to .addCopyOption"
				+ "()?)");
		if (!writeOpenOptions.contains(Objects.requireNonNull(translated)))
			throw new IllegalArgumentException("option " + translated + "is "
				+ "not a supported read option (did you forget to "
				+ ".addWriteOpenOption()?)");
		if (!writeTranslations.containsKey(option))
			writeTranslations.put(option, new HashSet<OpenOption>());
		writeTranslations.get(option).add(translated);
	}

	@Nonnull
	private Set<OpenOption> copyTranslations(final Set<CopyOption> options,
		final Map<CopyOption, Set<OpenOption>> map)
	{
		final Set<OpenOption> set = new HashSet<>();

		for (final CopyOption option: options) {
			if (!copyOptions.contains(Objects.requireNonNull(option)))
				throw new UnsupportedOptionException(option.toString());
			if (map.containsKey(option))
				set.addAll(map.get(option));
		}

		return set.isEmpty() ? Collections.<OpenOption>emptySet()
			: Collections.unmodifiableSet(set);
	}
}
