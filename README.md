# Sparesort

**Sparesort** is a Java library for performing sequential pattern mining, which is a data mining problem for analyzing sequencing or time related processes, e.g. customer purchase or web access patterns. 
This library implements BIDE, an efficient algorithm for mining frequent closed sequences.

## System Requirements

* Java Runtime Environment (JRE) 1.5 or higher

## Usage

### From console

The distribution contains an executable JAR (`sparesort-X.Y.Z.jar`).
You can simply try the argorithm by running `java -jar` command in your console.

    java -classpath commons-lang3-3.1.jar:slf4j-api-1.6.4.jar \
         -jar sparesort-X.Y.Z.jar \
         min_sup sequence1 sequence2 sequence3 ...

Note that dependent libraries ([Commons Lang](http://commons.apache.org/proper/commons-lang/) and [SLF4J](http://www.slf4j.org/)) are provided by the classpath argument.
This console application takes the following two kinds of arguments:

* The first argument `min_sup` is an absolute minimium support, an integer larger than 1.
* The other argumets `sequence1 sequence2 sequence3 ...` are paths to sequence files. Each file contains one sequence of string items separated by new lines.

After mining is finished, all frequent closed sequences qualified by the threshold and their frequency are printed to the standard output.

### Within your code

1. Create an instance of `jp.ac.titech.cs.se.sparesort.SequenceDatabase<T>` which supports `addSequence(List<T>)` and `addSequence(T...)` methods to load item sequences.

		// Create and load seqauence database
		SequenceDatabase<String> sdb = new SequenceDatabase<String>()
			.addSequence("C", "A", "A", "B", "C")
			.addSequence("A", "B", "C", "B")
			.addSequence("C", "A", "B", "C")
			.addSequence("A", "B", "B", "C", "A");

2. (Optional) Set an alternative mining strategy. When no strategy is given, `jp.ac.titech.cs.se.sparesort.bide.RecursiveBIDE<T>` is used as a default strategy to perform depth-first mining based on recursive calls.

		sdb.setMiningStrategy(new RecursiveBIDE<String>());

3. Execute `mineFrequentClosedSequences(int)` method giving an absolute minimum support threshold as its argument.
The return value is a mapping from frequent sequences found in the sequence database and their frequency.

		Map<List<String>, Integer> result = sdb.mineFrequentClosedSequences(2);

## Compile and build

Sparesort is built by using [Apache Maven](http://maven.apache.org/).
Before you proceed, download Maven and configure the environment variable `M2_HOME` properly.

1. Fork (or download) the source code.
2. Open your terminal or command prompt, move to the directory containing pom.xml and hit `mvn package` there.
3. After maven build succeeds, you will find `sparesort-X.Y.Z.jar` in `target/` subdirectory.

## References

BIDE algorithm was presented by Wang and Han in the follwoing paper:

* Jianyong Wang, Jiawei Han: “BIDE: Efficient mining of frequent closed sequences”. In Proceedings of the 20th International Conference on Data Engineering (ICDE 2004), 30 March - 2 April 2004, Boston, USA, pp. 79–90. [doi:10.1109/ICDE.2004.1319986](http://dx.doi.org/10.1109/ICDE.2004.1319986)

Sparesort is designed for stimulating researchers in software and data engineering.
If you use this library in your research and publications, please cite the following article as its source:

* Hiroshi Kazato, Shinpei Hayashi, Tsuyoshi Oshima, Shunsuke Miyata, Takashi Hoshino, Motoshi Saeki: "Extracting and Visualizing Implementation Structure of Features". In Proceedings of the 20th Asia-Pacific Software Engineering Conference (APSEC 2013). Bangkok, Thailand, dec, 2013.

## Copyright and license

Copyright (c) 2010-2012 [Saeki Lab.](http://www.se.cs.titech.ac.jp/) at [Tokyo Institute of Technology](http://www.titech.ac.jp/).  
Sparesort is an open source software, licensed under the [Apache Licese, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
