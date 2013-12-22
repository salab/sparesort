/*
 * Copyright (c) 2010-2012 Saeki Lab. at Tokyo Institute of Technology.
 * All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.ac.titech.cs.se.sparesort.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class RecursiveTaskRunner {

	private final ExecutorService executor;

	private final CompletionService<List<RecursiveTask>> queue;

	private final Set<Future<?>> waiting;

	public RecursiveTaskRunner() {
		this(Executors.newCachedThreadPool());
	}

	public RecursiveTaskRunner(int nThreads) {
		this(Executors.newFixedThreadPool(nThreads));
	}

	public RecursiveTaskRunner(ExecutorService executor) {
		this.executor = executor;
		this.queue = new ExecutorCompletionService<List<RecursiveTask>>(
				executor);
		this.waiting = new HashSet<Future<?>>();
	}

	public void run(RecursiveTask task) throws Exception {
		waiting.add(queue.submit(task));

		while (!waiting.isEmpty()) {
			Future<List<RecursiveTask>> completed = queue.take();
			waiting.remove(completed);

			List<RecursiveTask> subtasks = completed.get();
			if (subtasks != null) {
				for (RecursiveTask subtask : subtasks) {
					waiting.add(queue.submit(subtask));
				}
			}
		}
		executor.shutdown();
		executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	}

}
