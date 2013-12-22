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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import jp.ac.titech.cs.se.sparesort.ResultHandler;
import jp.ac.titech.cs.se.sparesort.MiningStrategy;
import jp.ac.titech.cs.se.sparesort.Sequence;
import jp.ac.titech.cs.se.sparesort.SequenceDatabase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BIDE<T> implements MiningStrategy<T> {

	protected static final Logger logger = LoggerFactory.getLogger(BIDE.class);

	protected List<List<T>> bide(SequenceDatabase<T> sdb, int minSup,
			ResultHandler<T> handler) {
		List<T> prefix = sdb.getPrefix();

		if (prefix != null && backScan(sdb)) {
			return Collections.emptyList();
		}

		SortedMap<T, Integer> frequencies = sdb.getItemFrequencies(minSup);
		if (isPrefixClosed(sdb, frequencies)) {
			synchronized (handler) {
				handler.handle(prefix, sdb.getSupportOfPrefix(), sdb);
			}
		}

		List<List<T>> prefixes = new ArrayList<List<T>>();
		for (Map.Entry<T, Integer> entry : frequencies.entrySet()) {
			List<T> extendedPrefix = new ArrayList<T>();
			if (prefix != null) {
				extendedPrefix.addAll(prefix);
			}
			extendedPrefix.add(entry.getKey());
			prefixes.add(extendedPrefix);
		}
		return prefixes;
	}

	protected boolean isPrefixClosed(SequenceDatabase<T> sdb,
			SortedMap<T, Integer> frequencies) {
		List<T> prefix = sdb.getPrefix();
		if (prefix == null) {
			return false;
		}

		int support = sdb.getSupportOfPrefix();
		if (logger.isDebugEnabled()) {
			logger.debug("Testing: {}:{}", prefix, support);
		}

		for (Map.Entry<T, Integer> entry : frequencies.entrySet()) {
			if (entry.getValue() == support) {
				if (logger.isTraceEnabled()) {
					logger.trace(
							"Open: The prefix can be extended by a forward extension item {}.",
							entry.getKey());
				}
				return false; /* Found a forward extension item */
			}
		}

		if (!hasBackwardExtensionItem(sdb)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Closed: {}:{}", prefix, support);
			}
			return true;
		}
		return false;
	}

	protected boolean hasBackwardExtensionItem(SequenceDatabase<T> sdb) {
		List<T> prefix = sdb.getPrefix();
		prefix_loop: for (int i = 0; i < prefix.size(); i++) {
			Set<T> intersection = null;

			for (Sequence<T> sequence : sdb.getSequences()) {
				List<T> mp_i = sequence.getMaximumPeriodOf(prefix, i);
				if (mp_i == null || mp_i.isEmpty()) {
					continue prefix_loop;
				}

				if (intersection == null) {
					intersection = new HashSet<T>(mp_i);
				} else {
					intersection.retainAll(mp_i);
					if (intersection.isEmpty()) {
						continue prefix_loop;
					}
				}
			}

			if (logger.isTraceEnabled()) {
				logger.trace(
						"Open: The prefix can be extended by backward extension items {}, found in each of the {} maximum period.",
						intersection, toOrdinal(i));
			}
			return true;
		}
		return false;
	}

	protected boolean backScan(SequenceDatabase<T> sdb) {
		List<T> prefix = sdb.getPrefix();
		prefix_loop: for (int i = 0; i < prefix.size(); i++) {
			Set<T> intersection = null;

			for (Sequence<T> sequence : sdb.getSequences()) {
				List<T> smp_i = sequence.getSemiMaximumPeriodOf(prefix, i);
				if (smp_i == null || smp_i.isEmpty()) {
					continue prefix_loop;
				}

				if (intersection == null) {
					intersection = new HashSet<T>(smp_i);
				} else {
					intersection.retainAll(smp_i);
					if (intersection.isEmpty()) {
						continue prefix_loop;
					}
				}
			}

			if (logger.isTraceEnabled()) {
				logger.trace(
						"Pruned: Search space was pruned by the BackScan method. Items {} exists in each of the {} semi-maximum period.",
						intersection, toOrdinal(i));
			}
			return true;
		}
		return false;
	}

	public static String toOrdinal(int i) {
		int mod = i % 10;
		return i + (mod == 1 ? "st" : mod == 2 ? "nd" : mod == 3 ? "rd" : "th");
	}

}
