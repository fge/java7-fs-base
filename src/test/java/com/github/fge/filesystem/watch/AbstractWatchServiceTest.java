/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.fge.filesystem.watch;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.Watchable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.fge.filesystem.watch.AbstractWatchService.BasicWatchKey;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/**
 * Tests for {@link AbstractWatchService}.
 *
 * @author Colin Decker
 */
public class AbstractWatchServiceTest {

  private AbstractWatchService watcher;

  @BeforeEach
  public void setUp() throws IOException {
    watcher = new AbstractWatchService() {};
  }

  @Test
  public void testNewWatcher() throws IOException {
    assertTrue(watcher.isOpen());
    assertNull(watcher.poll());
    assertTrue(watcher.queuedKeys().isEmpty());
    watcher.close();
    assertFalse(watcher.isOpen());
  }

  @Test
  public void testRegister() throws IOException {
    Watchable watchable = new StubWatchable();
    AbstractWatchService.BasicWatchKey key = watcher.register(watchable, new HashSet<>(Arrays.asList(ENTRY_CREATE)));
    assertTrue(key.isValid());
    assertTrue(key.pollEvents().isEmpty());
    assertTrue(key.subscribesTo(ENTRY_CREATE));
    assertFalse(key.subscribesTo(ENTRY_DELETE));
    assertEquals(watchable, key.watchable());
    assertEquals(BasicWatchKey.State.READY, key.state());
  }

  @Test
  public void testPostEvent() throws IOException {
    AbstractWatchService.BasicWatchKey key =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));

    AbstractWatchService.BasicWatchEvent<Path> event =
        new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null);
    key.post(event);
    key.signal();

    assertTrue(watcher.queuedKeys().contains(key));

    WatchKey retrievedKey = watcher.poll();
    assertEquals(key, retrievedKey);

    List<WatchEvent<?>> events = retrievedKey.pollEvents();
    assertEquals(1, events.size());
    assertEquals(event, events.get(0));

    // polling should have removed all events
    assertTrue(retrievedKey.pollEvents().isEmpty());
  }

  @Test
  public void testKeyStates() throws IOException {
    AbstractWatchService.BasicWatchKey key =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));

    AbstractWatchService.BasicWatchEvent<Path> event =
        new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null);
    assertEquals(BasicWatchKey.State.READY, key.state());
    key.post(event);
    key.signal();
    assertEquals(BasicWatchKey.State.SIGNALLED, key.state());

    AbstractWatchService.BasicWatchEvent<Path> event2 =
        new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null);
    key.post(event2);
    assertEquals(BasicWatchKey.State.SIGNALLED, key.state());

    // key was not queued twice
    assertTrue(watcher.queuedKeys().contains(key));
    assertIterableEquals(Arrays.asList(event, event2), watcher.poll().pollEvents());

    assertNull(watcher.poll());

    key.post(event);

    // still not added to queue; already signalled
    assertNull(watcher.poll());
    assertTrue(key.pollEvents().contains(event));

    key.reset();
    assertEquals(BasicWatchKey.State.READY, key.state());

    key.post(event2);
    key.signal();

    // now that it's reset it can be requeued
    assertEquals(key, watcher.poll());
  }

  @Test
  public void testKeyRequeuedOnResetIfEventsArePending() throws IOException {
    AbstractWatchService.BasicWatchKey key =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));
    key.post(new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null));
    key.signal();

    key = (AbstractWatchService.BasicWatchKey) watcher.poll();
    assertTrue(watcher.queuedKeys().isEmpty());

    assertEquals(1, key.pollEvents().size());

    key.post(new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null));
    assertTrue(watcher.queuedKeys().isEmpty());

    key.reset();
    assertEquals(BasicWatchKey.State.SIGNALLED, key.state());
    assertEquals(1, watcher.queuedKeys().size());
  }

  @Test
  public void testOverflow() throws IOException {
    AbstractWatchService.BasicWatchKey key =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));
    for (int i = 0; i < AbstractWatchService.BasicWatchKey.MAX_QUEUE_SIZE + 10; i++) {
      key.post(new AbstractWatchService.BasicWatchEvent<>(ENTRY_CREATE, 1, null));
    }
    key.signal();

    List<WatchEvent<?>> events = key.pollEvents();

    assertEquals(AbstractWatchService.BasicWatchKey.MAX_QUEUE_SIZE + 1, events.size());
    for (int i = 0; i < AbstractWatchService.BasicWatchKey.MAX_QUEUE_SIZE; i++) {
      assertEquals(ENTRY_CREATE, events.get(i).kind());
    }

    WatchEvent<?> lastEvent = events.get(AbstractWatchService.BasicWatchKey.MAX_QUEUE_SIZE);
    assertEquals(OVERFLOW, lastEvent.kind());
    assertEquals(10, lastEvent.count());
  }

  @Test
  public void testResetAfterCancelReturnsFalse() throws IOException {
    AbstractWatchService.BasicWatchKey key =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));
    key.signal();
    key.cancel();
    assertFalse(key.reset());
  }

  @Test
  public void testClosedWatcher() throws IOException, InterruptedException {
    AbstractWatchService.BasicWatchKey key1 =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_CREATE)));
    AbstractWatchService.BasicWatchKey key2 =
        watcher.register(new StubWatchable(), new HashSet<>(Arrays.asList(ENTRY_MODIFY)));

    assertTrue(key1.isValid());
    assertTrue(key2.isValid());

    watcher.close();

    assertFalse(key1.isValid());
    assertFalse(key2.isValid());
    assertFalse(key1.reset());
    assertFalse(key2.reset());

    try {
      watcher.poll();
      fail();
    } catch (ClosedWatchServiceException expected) {
    }

    try {
      watcher.poll(10, SECONDS);
      fail();
    } catch (ClosedWatchServiceException expected) {
    }

    try {
      watcher.take();
      fail();
    } catch (ClosedWatchServiceException expected) {
    }

    try {
      watcher.register(new StubWatchable(), Arrays.asList());
      fail();
    } catch (ClosedWatchServiceException expected) {
    }
  }

  // TODO(cgdecker): Test concurrent use of Watcher

  /** A fake {@link Watchable} for testing. */
  private static final class StubWatchable implements Watchable {

    @Override
    public WatchKey register(
        WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers)
        throws IOException {
      return register(watcher, events);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events)
        throws IOException {
      return ((AbstractWatchService) watcher).register(this, Arrays.asList(events));
    }
  }
}
