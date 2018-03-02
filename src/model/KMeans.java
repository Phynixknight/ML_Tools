package model;

public class KMeans {

	int K; // K class
	int N; // Doc number
	int D; // doc dimentions

	int[][] S; // doc sets
	int[] C; // class of every doc
	int[] CC; // class change to last class

	double[][] distance;
	double[][] center;

	double Threshold = 0.01;
	int Burn_in = 100;

	public KMeans(int[][] documents, int number_N, int number_k) {
		assert (N == documents.length);
		N = documents.length;
		D = documents[0].length;
		K = number_k;
		S = documents;
		C = new int[N];
		CC = new int[K];
		center = new double[K][D];
		distance = new double[N][K];
		if (N > 100000)
			Threshold = 0.001;
	}

	void arg(int burn_in, double thredhold) {
		Burn_in = burn_in;
		Threshold = thredhold;
	}

	void initcenter_allrand() {
		int[] count = new int[K]; // count how many elements in each cluster
		int k;
		for (int i = 0; i < N; i++) {
			k = (int) Math.random() * K;
			count[k]++;
			for (int j = 0; j < D; j++)
				center[k][j] += S[i][j];
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < D; j++) {
				center[i][j] /= count[i];
			}
		}
	}

	void initcenter_krand() {
		int index, k, d;
		for (k = 0; k < K; k++) {
			index = (int) (Math.random() * N);
			for (d = 0; d < D; d++)
				center[k][d] = S[index][d];
			System.out.println("init center k doc: " + index);
		}
	}

	void clusterring() {
		int k, d, n, change, iter = 0;
		for (k = 0; k < K; k++)
			CC[k] = 0;

		double last_dist, last_Class;
		double[][] new_center = new double[K][D];
		do {
			change = 0;
			iter++;

			for (n = 0; n < N; n++) {
				last_dist = Double.MAX_VALUE;
				last_Class = C[n];
				for (k = 0; k < K; k++) {
					distance[n][k] = distance_Euclidean(S[n], center[k]);
					C[n] = distance[n][k] < last_dist ? k : C[n];
					updateCenter(new_center[C[n]], S[n]);
					last_dist = distance[n][k];
					if (C[n] != last_Class) {
						change++;
						CC[C[n]]++;
					}
				}
			}

			for (k = 0; k < K; k++) {
				for (d = 0; d < D; d++) {
					center[k][d] = new_center[k][d] / CC[k];
					new_center[k][d] = 0;
				}
				CC[k] = 0;
			}

		} while (iter < Burn_in || change > Threshold * N);
		
		System.out.println("END! Iteration for " + iter + " times");
	}

	private double distance_Euclidean(int[] d, double[] c) {
		double dis = 0;
		for (int i = 0; i < d.length; i++) {
			dis += (d[i] - c[i]) * (d[i] - c[i]);
		}
		return dis;
	}

	private void updateCenter(double[] new_center, int[] s) {
		assert (new_center.length == s.length);
		assert (new_center.length == D);
		for (int i = 0; i < D; i++) {
			new_center[i] += s[i];
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[][] document = { { 1, 2, 3, 4, 5, 6, 70, 800 },
				{ 1, 2, 3, 4, 50, 600, 7, 8 }, { 1, 2, 30, 400, 5, 6, 7, 8 },
				{ 10, 200, 3, 4, 5, 6, 7, 8 }, { 1000, 2, 30, 4, 5, 6, 7, 8 },
				{ 1, 2, 300, 4, 50, 6, 7, 8 }, };

		KMeans km = new KMeans(document, document.length, 3);
		km.arg(20, 0);
		km.initcenter_krand();
		km.clusterring();

	}

}
