package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import util.Doc;
import util.StopWord;
import util.WordCount;

public class DataProcessing {
	File srcFile;
	File tagetDir;

	ArrayList<Doc> allDocs = new ArrayList<Doc>();
	HashMap<String, WordCount> keyword_id = new HashMap<String, WordCount>(); // output

	StopWord stopW;

	BufferedReader br;
	PrintWriter pw;

	public DataProcessing(String src, String tgtDirName, String stopwordsDic) {
		srcFile = new File(src);
		tagetDir = new File(tgtDirName);
		stopW = new StopWord(new File(stopwordsDic));
	}

	public void run() {
		try {
			long startTime = System.currentTimeMillis();
			dataClean(srcFile);
			long endCleanTime = System.currentTimeMillis();
			System.out.println("数据清洗用时：" + (endCleanTime - startTime) + "\n");
			printOutWordsId();
			long endOutWords = System.currentTimeMillis();
			System.out.println("输出words用时：" + (endOutWords - endCleanTime)
					+ "\n");
			calTFIDF();
			long endTFIDF = System.currentTimeMillis();
			System.out.println("计算TFIDF用时：" + (endTFIDF - endOutWords) + "\n");
			System.out.println("总用时：" + (endTFIDF - startTime));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void dataClean(File f) throws IOException {
		System.out.println("正在进行文件读入和数据清洗...");
		Doc d;
		String doc;
		String words[];

		br = new BufferedReader(new InputStreamReader(new FileInputStream(f),
				"utf-8"));
		while ((doc = br.readLine()) != null) {
			if (doc.matches("\\s*")) {
				continue;
			}
			d = new Doc();
			words = doc.split("\\s+");
			for (int i = 0; i < words.length; i++) {// 最后一个分类数字排除
				if (stopW.contain(words[i]))
					continue;
				else if (words[i].matches("[0-9]+"))
					continue;
				if (keyword_id.containsKey(words[i])) {
					keyword_id.get(words[i]).IncreaseCount();
				} else {
					keyword_id.put(words[i], new WordCount());
				}
				keyword_id.get(words[i]).addDoc(d.id);
				d.add(keyword_id.get(words[i]).id);
			}
			allDocs.add(d);
		}
		br.close();
		System.out.println("文件读入和数据清洗完毕！");
	}

	public void calTFIDF() throws FileNotFoundException {
		long start = System.currentTimeMillis();
		System.out.println("caling TF*IDF...");
		System.out.println("\tcaling 文档二范数...");
		calDocLength();
		long endMold = System.currentTimeMillis();
		System.out.println("\t文档长度计算完毕,用时：" + (endMold - start));
		System.out.println("\tcaling 单词IDF...");
		calIDF();
		long endIDF = System.currentTimeMillis();
		System.out.println("\tword IDF 计算完毕，用时：" + (endIDF - endMold));

		File f = new File(tagetDir, "TFIDF.txt");
		pw = new PrintWriter(new FileOutputStream(f));

		System.out.println("\tcaling 权值=TF*IDF，输出到\"" + f.getAbsolutePath()
				+ "\"...");
		for (Doc e : allDocs) {
			pw.print(e.id);
			for (Integer wordID : e.words.keySet()) {
				pw.print(" " + wordID + ":");
				pw.print(e.words.get(wordID) / e.mold * wordID_idf.get(wordID));
			}
			pw.println();
		}
		pw.close();
		System.out.println("TFIDF输出完毕！");
	}

	private void calDocLength() {
		for (Doc c : allDocs) {
			c.calMold();
		}
	}

	HashMap<Integer, Double> wordID_idf = new HashMap<Integer, Double>();

	private void calIDF() {
		for (String word : keyword_id.keySet()) {
			wordID_idf.put(
					keyword_id.get(word).id,
					Math.log((allDocs.size() + keyword_id.size())
							/ (keyword_id.get(word).docs.size() + 1)));
		}

	}

	void printOutWordsId() throws FileNotFoundException {// output all the word and index
		File f = new File(tagetDir, "words-id.txt");
		pw = new PrintWriter(new FileOutputStream(f));

		System.out.println("将单词和编号输出到" + f.getAbsolutePath());
		for (String word : keyword_id.keySet()) {
			pw.println(word + " " + keyword_id.get(word));
		}
		pw.close();
		System.out.println("单词和编号输出完毕！");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String srcDirName = "D:\\MyWorkspaceAtD\\BigDataCompetition\\class_splited\\keyword_class_unknow_splited";
		String tagetDirName = "D:\\MyWorkspaceAtD\\BigDataCompetition\\TFIDF_unknownclass_wordsplit\\";

		DataProcessing ti = new DataProcessing(srcDirName, tagetDirName,
				"./StopWordDic/stopwords.dic");
		ti.run();
	}

}
