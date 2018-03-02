package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class StopWord {

	private File StopWordsFile;

	HashSet<String> set_sw;

	public StopWord(File stopWordsFile) {
		set_sw = new HashSet<String>();
		StopWordsFile = stopWordsFile;
		LoadStopWords();
	}

	private void LoadStopWords() {
		BufferedReader br;
		String line;
		set_sw.add("");
		set_sw.add(" ");
		try {
			br = new BufferedReader(new FileReader(StopWordsFile));
			while ((line = br.readLine()) != null) {
				set_sw.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean contain(String s) {
		return set_sw.contains(s);
	}

}
