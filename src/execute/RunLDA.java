package execute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import model.LDAGibbsSampling;

public class RunLDA {

	private int[][] document;
	File doc;

	public RunLDA(String data, int D, int V) {
		doc = new File(data);
		document = new int[D][];
	}

	private void load() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(doc));
			String line;
			String[] term;
			int docid = 0;
			int wordnumber = 0;
			while ((line = br.readLine()) != null) {
				term = line.split("\\s+");
				document[docid] = new int[term.length];
				wordnumber += term.length;
				for (int i = 0; i < term.length; i++)
					document[docid][i] = Integer.parseInt(term[i]);
				docid++;
			}
			br.close();
			System.out.println("data loaded! doc:" + docid + "; word:" + wordnumber);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		System.out.println("loading data");
		load();
		double alpha = 2;
		double beta = .5;
		int burn_in = 2000;
		int iteration = 3000;
		int log_interval = 50;
		int vocabulary = 61188;
		int doc = 11269;
		int K = 30;
		LDAGibbsSampling lda = new LDAGibbsSampling(document, vocabulary, doc, K);
		lda.arg(alpha, beta, burn_in, iteration);
		lda.log(log_interval, ".case/20newsgroup/20new_for_lda/Dump_LDA");
		lda.init();
		lda.gibbs();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RunLDA runlda = new RunLDA(".case/20newsgroup/20new_for_lda/rain.lda.data", 11269, 0);
		runlda.run();
	}

}
