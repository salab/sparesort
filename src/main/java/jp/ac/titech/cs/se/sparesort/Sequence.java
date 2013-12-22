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

import java.util.Collections;
import java.util.List;

public class Sequence<T> {

	private final String id;

	private final List<T> events;

	private final int offset;

	public Sequence(String id, List<T> events) {
		super();
		this.id = id;
		this.events = Collections.unmodifiableList(events);
		this.offset = 0;
	}

	private Sequence(Sequence<T> parent, int offset) {
		super();
		this.id = parent.id;
		this.events = parent.events;
		this.offset = offset;
	}

	public Sequence<T> getProjectedSequenceWithRespectTo(List<T> prefix) {
		int offset = locateFirstInstanceOf(prefix);
		return (offset == -1) ? null : new Sequence<T>(this, offset);
	}

	public String getId() {
		return id;
	}

	public List<T> getEvents() {
		return (offset == 0) ? events : events.subList(offset, events.size());
	}

	private int locateFirstInstanceOf(List<T> prefix) {
		int length = events.size();
		int pos = 0;
		prefix_loop: for (T prefix_i : prefix) {
			while (pos < length) {
				if (prefix_i.equals(events.get(pos++))) {
					continue prefix_loop;
				}
			}
			return -1;
		}
		return pos;
	}

	private List<T> getLastInstanceOf(List<T> prefix) {
		int index = events.lastIndexOf(prefix.get(prefix.size() - 1));
		return (index >= 0) ? events.subList(0, index + 1) : null;
	}

	private int locateLastInLastAppearanceWithRespectTo(List<T> prefix, int i) {
		List<T> lastInstance = getLastInstanceOf(prefix);
		int pos = lastInstance.size();

		prefix_loop: for (int index = prefix.size() - 1; index >= i; index--) {
			T prefix_i = prefix.get(index);
			while (--pos >= 0) {
				if (prefix_i.equals(lastInstance.get(pos))) {
					continue prefix_loop;
				}
			}
			return -1;
		}
		return pos;
	}

	public List<T> getMaximumPeriodOf(List<T> prefix, int i) {
		int from = (i == 0) ? 0 : locateFirstInstanceOf(prefix.subList(0, i));
		int to = locateLastInLastAppearanceWithRespectTo(prefix, i);
		return (from <= to) ? events.subList(from, to) : null;
	}

	private int locateLastInFirstAppearanceWithRespectTo(List<T> prefix, int i) {
		int pos = offset;

		prefix_loop: for (int index = prefix.size() - 1; index >= i; index--) {
			T prefix_i = prefix.get(index);
			while (--pos >= 0) {
				if (prefix_i.equals(events.get(pos))) {
					continue prefix_loop;
				}
			}
			return -1;
		}
		return pos;
	}

	public List<T> getSemiMaximumPeriodOf(List<T> prefix, int i) {
		int from = (i == 0) ? 0 : locateFirstInstanceOf(prefix.subList(0, i));
		int to = locateLastInFirstAppearanceWithRespectTo(prefix, i);
		return (from <= to) ? events.subList(from, to) : null;
	}

}
