package com.github.fge.filesystem.path;

import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * This class that implements {@link Runnable} is used to continuously poll the watch
 * queue and this will receive all events which are registered with the WatchQueue.
 */
public class WatchQueue implements Runnable {

	private WatchService watcher;

	public WatchQueue(WatchService watcher) {
		
		this.watcher = watcher;
		
	}

	/**
	 * In order to implement a watch service, loop forever to
	 * ensure to take the next item from the queue.
	 */
	@Override
	public void run() {
		
		try {
			
			WatchKey key = watcher.take();
			
			while(key != null) {
				
				for (WatchEvent<?> event : key.pollEvents()) {
					
					System.out.printf("Received %s event for file: %s\n",
							event.kind(), event.context() );
					
				}
				
				if(!key.reset()) {
					
					break;
					
				}
				
				key = watcher.take();
			}
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			
		}
		
	}
}
