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
package jp.ac.titech.cs.se.sparesort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jp.ac.titech.cs.se.sparesort.bide.RecursiveBIDE;

public class SequenceDatabase<T> {

	private final List<T> prefix;

	private final List<Sequence<T>> sequences;

	private MiningStrategy<T> strategy;

	public SequenceDatabase() {
		this(null, new ArrayList<Sequence<T>>());
	}

	private SequenceDatabase(List<T> prefix, List<Sequence<T>> sequences) {
		this.prefix = prefix;
		this.sequences = sequences;
	}

	public SequenceDatabase<T> getProjectedDatabaseWithRespectTo(List<T> prefix) {
		List<Sequence<T>> projections = new ArrayList<Sequence<T>>();
		for (Sequence<T> sequence : sequences) {
			Sequence<T> ps = sequence.getProjectedSequenceWithRespectTo(prefix);
			if (ps != null) {
				projections.add(ps);
			}
		}
		return projections.isEmpty() ? null : new SequenceDatabase<T>(prefix,
				projections);
	}

	public List<Sequence<T>> getSequences() {
		return sequences;
	}

	public SequenceDatabase<T> addSequence(String id, List<T> events) {
		sequences.add(new Sequence<T>(id, events));
		return this;
	}

	public SequenceDatabase<T> addSequence(List<T> events) {
		return addSequence(String.valueOf(getSupportOfPrefix() + 1), events);
	}

	public SequenceDatabase<T> addSequence(T... events) {
		return addSequence(Arrays.asList(events));
	}

	public List<T> getPrefix() {
		return prefix;
	}

	public int getSupportOfPrefix() {
		return (sequences == null) ? 0 : sequences.size();
	}

	public MiningStrategy<T> getMiningStrategy() {
		if (strategy == null) {
			strategy = new RecursiveBIDE<T>();
		}
		return strategy;
	}

	public void setMiningStrategy(MiningStrategy<T> strategy) {
		this.strategy = strategy;
	}

	public Map<List<T>, Integer> mineFrequentClosedSequences(int minSup)
			throws Exception {
		final Map<List<T>, Integer> result = new HashMap<List<T>, Integer>();

		mineFrequentClosedSequences(minSup, new ResultHandler<T>() {
			public void handle(List<T> sequence, int frequency,
					SequenceDatabase<T> sdb) {
				result.put(sequence, frequency);
			}
		});

		return result;
	}

	public void mineFrequentClosedSequences(int minSup, ResultHandler<T> handler)
			throws Exception {
		getMiningStrategy().mineFrequentClosedSequences(this, minSup, handler);
	}

	public SortedMap<T, Integer> getItemFrequencies(int minSup) {
		SortedMap<T, Integer> frequencies = new TreeMap<T, Integer>();
		for (Sequence<T> sequence : sequences) {
			for (T item : new HashSet<T>(sequence.getEvents())) {
				int f = frequencies.containsKey(item) ? frequencies.get(item)
						: 0;
				frequencies.put(item, ++f);
			}
		}

		Iterator<Integer> iter = frequencies.values().iterator();
		while (iter.hasNext()) {
			if (iter.next() < minSup) {
				iter.remove();
			}
		}
		return frequencies;
	}

}
