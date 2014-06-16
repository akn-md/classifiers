package nayak.Utility;

import java.util.Random;

import Jama.Matrix;

/**
 * Taken from:
 * https://code.google.com/p/wordsimilarity/source/browse/trunk/src/tools/VectorTools.java
 * b/c I'm lazy - will optimize with Jama later
 * 
 * @author  Brent Kievit-Kylar
 *
 */
public class MatrixUtil {

	public static double sum(Matrix a) {
		double sum = 0;
		double[] rowPacked = a.getRowPackedCopy();
		for (int x = 0; x < rowPacked.length; x++) {
			sum += rowPacked[x];
		}
		return sum;
	}

	public static Matrix max(Matrix m, double b) {
		double[][] a = m.getArray();
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = Math.max(a[x][y], b);
			}
		}
		return new Matrix(ret);
	}

	public static double[][] randn(int l, int w) {
		Random rand = new Random();
		double[][] ret = new double[l][w];
		for (int x = 0; x < l; x++) {
			for (int y = 0; y < w; y++) {
				ret[x][y] = rand.nextGaussian();
			}
		}
		return ret;
	}

	public static double[][] pow(double[][] from, double power) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				ret[x][y] = Math.pow(from[x][y], power);
			}
		}
		return ret;
	}

	public static double[] sumRow(double[][] a) {
		double[] sum = new double[a.length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				sum[x] += a[x][y];
			}
		}
		return sum;

	}

	public static double[][] to2D(double[] vecs) {
		double[][] t = new double[vecs.length][1];
		for (int x = 0; x < vecs.length; x++) {
			t[x][0] = vecs[x];
		}
		return t;
	}

	public static double[][] addAsRow(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = a[x][y] + b[0][y];
			}
		}
		return ret;
	}

	public static double[][] addAsCollumn(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = a[x][y] + b[x][0];
			}
		}
		return ret;
	}

	public static double[][] pointDiv(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = a[x][y] / b[x][y];
			}
		}
		return ret;
	}

	public static double[][] ones(int len, int width) {
		double[][] ret = new double[len][width];
		for (int x = 0; x < len; x++) {
			for (int y = 0; y < width; y++) {
				ret[x][y] = 1;
			}
		}
		return ret;
	}

	public static double[][] getAdd(double[][] from, double[][] to) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				ret[x][y] = from[x][y] + to[x][y];
			}
		}
		return ret;
	}

	public static double[][] pointMult(double[][] a, double[][] b) {
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = a[x][y] * b[x][y];
			}
		}
		return ret;
	}

	public static double[][] diag(double[] a) {
		double[][] ret = new double[a.length][a.length];
		for (int i = 0; i < a.length; i++) {
			ret[i][i] = a[i];
		}
		return ret;
	}

	public static double[][] sign(double[][] from) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				ret[x][y] = Math.signum(from[x][y]);
			}
		}
		return ret;
	}

	public static double[][] notEqual(double[][] from, double[][] to) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				double dist = from[x][y] - to[x][y];
				if (dist > .1 || dist < -.1) {
					ret[x][y] = 1;
					;
				} else {
					ret[x][y] = 0;
				}
			}
		}
		return ret;
	}

	public static double[][] getAdd(double[][] from, double to) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				ret[x][y] = from[x][y] + to;
			}
		}
		return ret;
	}

	public static double[][] mult(double[][] a, double b) {
		double[][] ret = new double[a.length][a[0].length];
		for (int x = 0; x < a.length; x++) {
			for (int y = 0; y < a[0].length; y++) {
				ret[x][y] = a[x][y] * b;
			}
		}
		return ret;
	}

	public static double[][] equal(double[][] from, double[][] to) {
		double[][] ret = new double[from.length][from[0].length];
		for (int x = 0; x < from.length; x++) {
			for (int y = 0; y < from[0].length; y++) {
				double dist = from[x][y] - to[x][y];
				if (dist > .1 || dist < -.1) {
					ret[x][y] = 0;
				} else {
					ret[x][y] = 1;
				}
			}
		}
		return ret;
	}
}
