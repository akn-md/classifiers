package nayak.Data;

import java.util.Random;

import nayak.Utility.Print;

/**
 * Basic Data Preprocessing Class
 * 
 * Features:
 * -unknown replacement with mean
 * -feature scaling (mean normalization)
 * -adding column of ones (for regression)
 * -polynomial features
 * 
 * @author Ashwin K Nayak
 *
 */
public class Preprocessor {

	double[][] data;
	double[] means;
	double[] ranges;
	double unknown;

	public Preprocessor(double[][] data, double unknown) {
		this.data = data;
		this.unknown = unknown;
		init();
	}

	private void init() {
		means = new double[data[0].length];
		ranges = new double[data[0].length];

		for (int i = 0; i < data[0].length; i++) {
			int count = 0;
			double mean = 0.0;
			double min = Integer.MAX_VALUE;
			double max = Integer.MIN_VALUE;
			for (int j = 0; j < data.length; j++) {
				double d = data[j][i];
				if (d != unknown) {
					mean += d;
					min = (d < min) ? d : min;
					max = (d > max) ? d : max;
					count++;
				}
			}
			mean /= count;
			double range = max - min;
			means[i] = mean;
			ranges[i] = range;
		}
	}

	/**
	 * Replace unknown values with the feature mean
	 */
	public void replaceUnknowns() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				if (data[i][j] == unknown)
					data[i][j] = means[j];
			}
		}
	}

	/**
	 * val = (original - mean)/range
	 */
	public void scaleFeatures() {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				double d = data[i][j];
				d -= means[j];
				d /= ranges[j];
				data[i][j] = d;
			}
		}
	}

	/**
	 * Adds "num" polynomial features of degree "degree". 
	 * If num < numFeatures and seed > 0, random features are chosen (no duplicates).
	 * If seed = -1, the first num features are chosen.
	 * 
	 * @param num
	 * @param degree
	 */
	public static double[][] addPolynomialFeatures(double[][] data, int num, int degree, long seed) {
		int numFeatures = data[0].length;
		int[] features = null;

		if (num == numFeatures)
			seed = -1;

		if (seed != -1) {
			Random random = new Random(seed);
			features = new int[num];
			boolean[] taken = new boolean[numFeatures];
			for (int i = 0; i < features.length; i++) {
				int index;
				do {
					index = (int) (random.nextDouble() * numFeatures);
				} while (taken[index] == true);
				taken[index] = true;
				features[i] = index;
			}
			Print.print(features);
		}

		double[][] adjustedData = new double[data.length][data[0].length + num];

		for (int i = 0; i < adjustedData.length; i++) {
			for (int j = 0; j < adjustedData[0].length; j++) {
				if (j < data[0].length) {
					adjustedData[i][j] = data[i][j];
				} else {
					int index = j - data[0].length;
					if (seed != -1)
						index = features[index];

					double d = data[i][index];
					double val = Math.pow(d, degree);

					adjustedData[i][j] = val;
				}
			}
		}

		return adjustedData;
	}

	/**
	 * Prepends a column of ones to the data (for regression).
	 */
	public static void addOnes(double[][] data) {
		double[][] d = new double[data.length][data[0].length + 1];
		for (int i = 0; i < data.length; i++) {
			d[i][0] = 1.0;
			System.arraycopy(data[i], 0, d[i], 1, data[i].length);
			data[i] = new double[d[i].length];
			System.arraycopy(d[i], 0, data[i], 0, data[i].length);
		}
	}
	
	public static double[] getCol(double[][] data, int col) {
		double[] d = new double[data.length];
		
		for(int i = 0; i < d.length; i++) {
			double dd = data[i][col];
			d[i] = dd;
		}
		
		return d;
	}
}
