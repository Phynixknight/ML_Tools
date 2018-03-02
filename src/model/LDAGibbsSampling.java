/**
 * @file LDAGibbsSampling.java
 * @date 2015-3-19
 */
package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * @time 04:28:00
 * @pro MyLDA
 * @package com.snail.ml.model.lda
 * @version 0.0.1
 */
public class LDAGibbsSampling {

	int V;// vocabulary size
	int K;// topic size
	int D;// document size

	int[][] Doc;// non duplicate document-term_index matrix
	int[][] Doc_n; // document-term_number
	int[][] Z;// document-topic matrix

	int[][] Phi; // topic-word Transposed matrix
	int[][] Theta; // doc-topic matrix
	int[] Ntw; // word count in topic

	int Burn_in;
	int Iterations;
	double Alpha;
	double Beta;

	int LOG_Interval = 50;
	File LOG_Dir;

	public LDAGibbsSampling(int[][] document, int voc_number, int doc_number,
			int topic_number) {
		Doc = document;
		V = voc_number;
		K = topic_number;
		D = doc_number;
	}

	public void arg(double alpha, double beta, int burn_in, int iteration) {
		assert (iteration > burn_in);
		Iterations = iteration;
		Burn_in = burn_in;
		Alpha = alpha;
		Beta = beta;
	}

	public void log(int log_interval, String log_dir) {
		LOG_Interval = 50;
		LOG_Dir = new File(log_dir);
		if (!LOG_Dir.exists())
			LOG_Dir.mkdirs();
	}

	public void init() {
		Phi = new int[V][K];
		Theta = new int[D][K];
		Ntw = new int[K];
		Z = new int[D][];
		for (int m = 0; m < D; m++) {
			Z[m] = new int[Doc[m].length];
			for (int n = 0; n < Doc[m].length; n++) {
				int z = (int) (Math.random() * K);
				Z[m][n] = z;
				Phi[Doc[m][n]][z]++;
				Theta[m][z]++;
				Ntw[z]++;
			}
		}

		// Ntw+VB
		for (int z = 0; z < K; z++) {
			Ntw[z] += V * Beta;
		}
	}

	public void gibbs() {
		for (int it = 0; it < Iterations; it++) {
			for (int m = 0; m < D; m++)
				for (int n = 0; n < Z[m].length; n++) {
					Z[m][n] = fullCondition(m, n);
				}

			if (it > Burn_in && it % LOG_Interval == 0) {
				printDump(it);
			} else if (it <= Burn_in && it % LOG_Interval == 0) {
				System.out.println("gibbs is going iter " + it);
			}
		}
	}

	private int fullCondition(int m, int n) {
		int z = Z[m][n];
		int t = Doc[m][n];
		Phi[t][z]--;
		Ntw[z]--;
		Theta[m][z]--;

		double[] p = new double[K];
		for (int k = 0; k < K; k++) {
			p[k] = (Phi[t][k] + Beta) * (Theta[m][k] + Alpha) / Ntw[k]
					+ (k > 0 ? p[k - 1] : .0);
		}

		double rand = Math.random() * p[K - 1];
		for (z = 0; z < K; z++) {
			if (rand < p[z])
				break;
		}

		Phi[t][z]++;
		Theta[m][z]++;
		Ntw[z]++;
		return z;
	}

	private void printDump(int it) {
		PrintWriter pw;
		System.out.println("iteration " + it + " write dump files");
		try {
			pw = new PrintWriter(new FileOutputStream(new File(LOG_Dir,
					"Dump_Phi_top" + K + "iter" + it)));
			printMatrix(Phi, pw, true);
			pw.close();

			pw = new PrintWriter(new FileOutputStream(new File(LOG_Dir,
					"Dump_Theta_top" + K + "iter" + it)));
			printMatrix(Theta, pw, false);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void printMatrix(int[][] m, PrintWriter p, boolean transpose) {
		if (transpose) {
			for (int j = 0; j < m[0].length; j++) {
				for (int i = 0; i < m.length; i++) {
					p.append(m[i][j] + " ");
				}
				p.println();
			}
		} else {
			for (int[] i : m) {
				for (int j : i) {
					p.append(j + " ");
				}
				p.println();
			}
		}
		p.flush();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[][] document = {
				{ 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 1, 4, 3, 2, 3, 6 },
				{ 2, 2, 4, 2, 4, 2, 2, 2, 2, 4, 2, 2 },
				{ 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 1, 6, 5, 6, 0, 0 },
				{ 5, 6, 6, 2, 3, 3, 6, 5, 6, 2, 2, 6, 5, 6, 6, 6, 0 },
				{ 2, 2, 4, 4, 4, 4, 1, 5, 5, 5, 5, 5, 5, 1, 1, 1, 1, 0 },
				{ 5, 4, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2 } };

		double alpha = 2;
		double beta = .5;
		int burn_in = 2000;
		int iteration = 3000;
		LDAGibbsSampling lda = new LDAGibbsSampling(document, 7, 6, 2);
		lda.arg(alpha, beta, burn_in, iteration);
		lda.log(50, "./log/Dump_LDA");
		lda.init();
		lda.gibbs();
	}

}
