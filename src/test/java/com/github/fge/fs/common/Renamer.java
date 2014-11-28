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

package com.github.fge.fs.common;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public final class Renamer
{
    private static final Joiner JOINER = Joiner.on(" - ");

    private static final class RenamerVisitor
        extends SimpleFileVisitor<Path>
    {
        private final Path baseDir;

        private RenamerVisitor(final Path baseDir)
        {
            this.baseDir = baseDir;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attrs)
            throws IOException
        {
            final Path relpath = baseDir.relativize(file);
            final String targetName
                = JOINER.join(Iterables.transform(relpath,
                Functions.toStringFunction()));
            final Path dstPath = baseDir.resolve(targetName);
            System.out.printf("'%s' -> '%s'\n", file, dstPath);
            return FileVisitResult.CONTINUE;
        }
    }

    public static void main(final String... args)
        throws IOException
    {
        final Path baseDir = Paths.get("/home/fge/tmp/jsr203/docs");
        Files.walkFileTree(baseDir, new RenamerVisitor(baseDir));
    }
}
