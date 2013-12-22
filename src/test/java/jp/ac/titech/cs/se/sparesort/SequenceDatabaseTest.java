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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jp.ac.titech.cs.se.sparesort.bide.ConcurrentBIDE;
import jp.ac.titech.cs.se.sparesort.bide.RecursiveBIDE;

import org.junit.Test;

public class SequenceDatabaseTest {

	@Test
	public void testRecursiveBIDE() throws Exception {
		SequenceDatabase<String> fixture = new SequenceDatabase<String>()
				.addSequence("C", "A", "A", "B", "C")
				.addSequence("A", "B", "C", "B")
				.addSequence("C", "A", "B", "C")
				.addSequence("A", "B", "B", "C", "A");

		fixture.setMiningStrategy(new RecursiveBIDE<String>());
		Map<List<String>, Integer> result = fixture.mineFrequentClosedSequences(2);

		assertNotNull(result);
		assertEquals(6, result.size());
		assertTrue(result.containsKey(Arrays.asList("A", "A")));
		assertTrue(result.containsKey(Arrays.asList("A", "B", "B")));
		assertTrue(result.containsKey(Arrays.asList("A", "B", "C")));
		assertTrue(result.containsKey(Arrays.asList("C", "A")));
		assertTrue(result.containsKey(Arrays.asList("C", "A", "B", "C")));
		assertTrue(result.containsKey(Arrays.asList("C", "B")));
	}

	@Test
	public void testConcurrentBIDE() throws Exception {
		SequenceDatabase<String> fixture = new SequenceDatabase<String>()
				.addSequence("C", "A", "A", "B", "C")
				.addSequence("A", "B", "C", "B")
				.addSequence("C", "A", "B", "C")
				.addSequence("A", "B", "B", "C", "A");

		fixture.setMiningStrategy(new ConcurrentBIDE<String>());
		Map<List<String>, Integer> result = fixture.mineFrequentClosedSequences(2);

		assertNotNull(result);
		assertEquals(6, result.size());
		assertTrue(result.containsKey(Arrays.asList("A", "A")));
		assertTrue(result.containsKey(Arrays.asList("A", "B", "B")));
		assertTrue(result.containsKey(Arrays.asList("A", "B", "C")));
		assertTrue(result.containsKey(Arrays.asList("C", "A")));
		assertTrue(result.containsKey(Arrays.asList("C", "A", "B", "C")));
		assertTrue(result.containsKey(Arrays.asList("C", "B")));
	}
}
