package util;

import java.util.HashMap;

public class Doc {
	public static int in_id = 0;

	public final int id = in_id++;

	public HashMap<Integer, Integer> words; //words id_countInThisDoc
	public double mold = 0;
	public int docLength = 0;

	// int max = 0; //count max word

	public int getDocLength() {
		return docLength;
	}

//	public HashMap<String, Integer> getWords() {
//		return words;
//	}

	public Doc() {
		words = new HashMap<Integer, Integer>();
	}

	public void add(Integer _word) {
		docLength++;
		if (words.containsKey(_word))
			words.put(_word, words.get(_word) + 1);
		else {
			words.put(_word, 1);
		}
		// max = max < words.get(_word) ? words.get(_word) : max;// not using tempory
	}

	public int size() {
		return words.size();
	}

	public void calMold() {
		mold = 0;
		for (Integer i : words.values()) {
			mold += Math.pow(i, 2);
		}
		mold = Math.sqrt(mold);
	}

	public boolean contains(String s) {
		return words.containsKey(s);
	}

	public int getValue(String s) {
		return words.get(s);
	}

	public double getMold() {
		return mold;
	}

}
