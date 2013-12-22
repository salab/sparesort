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
package jp.ac.titech.cs.se.sparesort.bide;

import java.util.ArrayList;
import java.util.List;

import jp.ac.titech.cs.se.sparesort.ResultHandler;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;
import jp.ac.titech.cs.se.sparesort.util.RecursiveTask;
import jp.ac.titech.cs.se.sparesort.util.RecursiveTaskRunner;

public class ConcurrentBIDE<T> extends BIDE<T> {

	public void mineFrequentClosedSequences(SequenceDatabase<T> sdb,
			int minSup, ResultHandler<T> handler) throws Exception {
		RecursiveTaskRunner runner = new RecursiveTaskRunner();
		runner.run(new BIDETask(sdb, minSup, handler));
	}

	private class BIDETask implements RecursiveTask {

		private final SequenceDatabase<T> sdb;

		private final int minSup;

		private final ResultHandler<T> handler;

		public BIDETask(SequenceDatabase<T> sdb, int minSup,
				ResultHandler<T> handler) {
			super();
			this.sdb = sdb;
			this.minSup = minSup;
			this.handler = handler;
		}

		public List<RecursiveTask> call() {
			List<RecursiveTask> subtasks = new ArrayList<RecursiveTask>();
			for (List<T> extendedPrefix : bide(sdb, minSup, handler)) {
				subtasks.add(new BIDETask(sdb
						.getProjectedDatabaseWithRespectTo(extendedPrefix),
						minSup, handler));
			}
			return subtasks;
		}
	}

}
