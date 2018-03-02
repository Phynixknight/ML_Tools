/**
 * 
 */
package util;

import java.util.HashSet;

/**
 * @author snail
 * @creation 2013-10-30
 */
public class WordCount implements Comparable<WordCount> {

	public static int globleWordCount = 0;
	public final int id = globleWordCount++; // index
	public int count; // sum count in corpus
	public HashSet<Integer> docs; // word's article index in corpus

	// public double idf = 1;

	// Count Class mapping word determined by Map so rename to WordCount
	public WordCount() {
		count = 0;
		docs = new HashSet<Integer>();
	}

	public void addDoc(int docID) {
		docs.add(docID);
	}

	public void IncreaseCount() {
		count++;
	}

	@Override
	public String toString() {
		return id + "";
	}

	@Override
	public int compareTo(WordCount o) {
		return id - o.id;
	}
}
