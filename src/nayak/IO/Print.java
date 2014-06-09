package nayak.IO;

import java.util.Arrays;

public class Print {

	public static void print(double[] d) {
		System.out.println(Arrays.toString(d));
	}

	public static void print(double[][] d) {
		for(int i = 0; i < d.length; i++) {
			System.out.println("Row " + i + ": " + Arrays.toString(d[i]));
		}
	}
}
