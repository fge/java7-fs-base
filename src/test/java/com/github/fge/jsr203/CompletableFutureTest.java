package com.github.fge.jsr203;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

public final class CompletableFutureTest
{
    private interface Foo
    {
        Integer foo(Integer arg)
            throws IOException;

        static Supplier<Integer> of(final Foo foo, final Integer arg)
        {
            return () -> {
                try {
                    return foo.foo(arg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
        }
    }

    public static void main(final String... args)
    {
        final Executor executor = Runnable::run;

        final Foo foo1 = arg -> 1;
        final Supplier<Integer> supplier1 = Foo.of(foo1, 2);

        final IOException e = new IOException();
        final Foo foo2 = arg -> { throw e; };
        final Supplier<Integer> supplier2 = Foo.of(foo2, 2);

        final CompletableFuture<Integer> f = CompletableFuture
            .supplyAsync(supplier2, executor)
            .handle((ret, throwable) -> {
                if (throwable != null)
                    System.out.println(throwable.getCause().getCause().getClass().getSimpleName());
                return throwable == null ? ret : -1;
            });

        System.out.println(f.join());
    }
}
