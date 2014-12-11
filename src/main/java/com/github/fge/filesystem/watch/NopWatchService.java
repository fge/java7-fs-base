package com.github.fge.filesystem.watch;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A no-op {@link WatchService} implementation
 *
 * <p>As its name tells, this implementation will never return any {@link
 * WatchKey}s. This means that if you try and {@link #take()} from it, you will
 * sleep forever until the watch service is closed, and will be greeted with a
 * {@link ClosedWatchServiceException}.</p>
 */

@Immutable // therefore thread safe
public final class NopWatchService
    implements WatchService
{
    /*
     * Closed sentinel.
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);
    /*
     * All callers to poll() with timeout and take() will .await() on this
     * latch, which will only be .countDown()ed when the service is closed.
     */
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Path path;

    public NopWatchService(@Nonnull final Path path)
    {
        this.path = Objects.requireNonNull(path);
    }

    @Override
    public void close()
        throws IOException
    {
        closed.set(true);
        /*
         * This is idempotent; as per the documentation, .countDown() on a latch
         * whose count is 0 will do nothing.
         */
        latch.countDown();
    }

    @Override
    public WatchKey poll()
    {
        if (closed.get())
            throw new ClosedWatchServiceException();

        return null;
    }

    @Override
    public WatchKey poll(final long timeout, final TimeUnit unit)
        throws InterruptedException
    {
        /*
         * No need to check for the service being closed before waiting here;
         * if the service is closed, the latch is "open" and .await() will do
         * nothing. If it is not closed, let the caller sleep.
         */
        latch.await(timeout, unit);

        if (closed.get())
            throw new ClosedWatchServiceException();

        return null;
    }

    @Override
    public WatchKey take()
        throws InterruptedException
    {
        /*
         * Since this method will wait until a key is available, and this watch
         * service never provides any, it means it will wait permantenly. The
         * only possibility of this method waking up is when close() is called,
         * which means the watch service itself is closed, therefore...
         */
        latch.await();
        throw new ClosedWatchServiceException();
    }

    public Path getPath()
    {
        return path;
    }
}
