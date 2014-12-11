package com.github.fge.filesystem.watch;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

/**
 * A no-op {@link WatchService} implementation
 *
 */

public class NopWatchService implements WatchService {
	
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private final CountDownLatch latch = new CountDownLatch(1);
	private final Path path;
	
	public NopWatchService (@Nonnull final Path path) {
		this.path = Objects.requireNonNull(path);
		
	}

	@Override
	public void close() throws IOException {
		closed.set(true); 		
		latch.countDown();
	}

	@Override
	public WatchKey poll() {
		if(closed.get())
			throw new ClosedWatchServiceException();
		
		return null;
	}

	@Override
	public WatchKey poll(long timeout, TimeUnit unit)
			throws InterruptedException {
		latch.await(timeout,unit);
		if(closed.get())
			throw new ClosedWatchServiceException();
		
		return null;
	}

	@Override
	public WatchKey take() throws InterruptedException {
		latch.await();
		throw new ClosedWatchServiceException();
	}

	/**
     * Returns the path that invoked this watch service.
     *
     * @return The path that created this file system.
     */
	public Path getPath() {
		return path;
	}

}
