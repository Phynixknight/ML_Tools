package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import util.StopWord;

public class DataProcessingForLDA {
	File srcFile;
	File srcDir;
	File tagetDir;

	int[][] docs;
	HashMap<String, Integer> term = new HashMap<String, Integer>();
	HashMap<String, Integer> topic = new HashMap<String, Integer>();

	StopWord stopW;

	BufferedReader br;
	PrintWriter pw;

	public DataProcessingForLDA(String src, String tgtDirName,
			String stopwordsDic) {
		srcDir = new File(src);
		tagetDir = new File(tgtDirName);
		stopW = new StopWord(new File(stopwordsDic));
	}

	public void run() throws IOException {
		File datafile = new File(tagetDir, "docs.dat");
		File wordsfile = new File(tagetDir, "words_index.txt");
		File topicsfile = new File(tagetDir, "topic_index.txt");
		pw = new PrintWriter(new FileOutputStream(datafile));
		int topic = 0;
		String line;
		String[] words;
		for (File top : srcDir.listFiles()) {
			for (File f : top.listFiles()) {
				br = new BufferedReader(new FileReader(f));
				while ((line = br.readLine()) != null) {
					if (line.matches("\\s*")) continue;
					words = line.split("\\s+");
					if(words[0].endsWith(":")) continue;
					if(words.length > 1 && words[1].endsWith(":")) continue;
					if(words.length < 3) continue;
				}
			}
			topic++;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
