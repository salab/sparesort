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

import java.util.List;

import jp.ac.titech.cs.se.sparesort.ResultHandler;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;

public class RecursiveBIDE<T> extends BIDE<T> {

	public void mineFrequentClosedSequences(SequenceDatabase<T> sdb,
			int minSup, ResultHandler<T> handler) throws Exception {
		applyBIDE(sdb, minSup, handler, 0, 100);
	}

	private void applyBIDE(SequenceDatabase<T> sdb, int minSup,
			ResultHandler<T> handler, double minProgress, double maxProgress) {
		double progress = minProgress;

		List<List<T>> prefixes = bide(sdb, minSup, handler);
		int size = prefixes.size();
		for (int i = 0; i < size; i++) {
			double nextProgress = minProgress + (maxProgress - minProgress)
					* ((double) (i + 1)) / ((double) size);
			applyBIDE(sdb.getProjectedDatabaseWithRespectTo(prefixes.get(i)),
					minSup, handler, progress, nextProgress);
			progress = nextProgress;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Progress: %.3f%%", progress));
		}
	}

}
