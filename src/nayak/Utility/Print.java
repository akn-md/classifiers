package nayak.Utility;

import java.util.Arrays;

import Jama.Matrix;

/**
 * 
 * @author Ashwin K Nayak
 *
 */
public class Print {

	public static void print(double[] d) {
		System.out.println(Arrays.toString(d));
	}

	public static void print(int[] d) {
		System.out.println(Arrays.toString(d));
	}

	public static void print(double[][] d, int rows) {
		for (int i = 0; i < rows; i++) {
			System.out.println("Row " + i + ": " + Arrays.toString(d[i]));
		}
	}

	public static void print(double[][] d) {
		print(new Matrix(d));
	}
	public static void print(Matrix m) {
		System.out.println("=====");
		for (int i = 0; i < m.getRowDimension(); i++) {
			System.out.print("Row " + i + "\t");
			for (int j = 0; j < m.getColumnDimension(); j++) {
				System.out.print(m.get(i, j) + "\t");
			}
			System.out.println();
		}
	}
}
