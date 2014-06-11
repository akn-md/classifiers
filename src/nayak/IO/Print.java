package nayak.IO;

import java.util.Arrays;

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
		for(int i = 0; i < rows; i++) {
			System.out.println("Row " + i + ": " + Arrays.toString(d[i]));
		}
	}
}
