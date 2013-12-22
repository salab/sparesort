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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		SequenceDatabase<String> sdb = new SequenceDatabase<String>();

		int minSup = Integer.parseInt(args[0]);
		for (int i = 1; i < args.length; i++) {
			sdb.addSequence(loadStringListFromFile(args[i]));
		}

		Map<List<String>, Integer> result = sdb.mineFrequentClosedSequences(minSup);
		for (Map.Entry<List<String>, Integer> entry : result.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

	public static List<String> loadStringListFromFile(String path)
			throws Exception {
		List<String> events = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new FileReader(path));

		String line = null;
		while ((line = reader.readLine()) != null) {
			events.add(StringUtils.chomp(line));
		}

		reader.close();
		return events;
	}

}
