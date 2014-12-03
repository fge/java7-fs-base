## Read me first

This project is licensed under both LGPLv3 and ASL 2.0. See file LICENSE for
more details.

## What this is

This is a package designed to ease the creation of custom Java 7
[`FileSystem`](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html)s.

## JRE restrictions

You **must** use either the JDK provided by Oracle or OpenJDK. At this moment,
this package depends on a package in... `sun.nio.fs`. More precisely, it depends
on it for generating patterns suitable for use in a glob
[`PathMatcher`](https://docs.oracle.com/javase/7/docs/api/java/nio/file/PathMatcher.html).

