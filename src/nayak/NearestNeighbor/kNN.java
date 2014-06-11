package nayak.NearestNeighbor;
/**
 * Implementation of k-Nearest Neighbor.
 * 
 * Features:
 * -Euclidean Distance
 * 
 * To Add:
 * -the k part
 * -Feature Weighting
 * 
 * @author Ashwin K Nayak
 *
 */
public class kNN {

	public static void main(String[] args) {
	}

	double[][] data;
	double[] labels;
	double[] testInstance;

	public kNN(double[][] data, double[] labels) {
		this.data = data;
		this.labels = labels;
	}

	public double classify(double[] instance) {
		this.testInstance = instance;
		double minDistance = Double.MAX_VALUE;
		double label = -1;
		double[] minPoint = null;

		for (int i = 0; i < data.length; i++) {
			double d = distance(i);
			if (d < minDistance) {
				minDistance = d;
				label = labels[i];
				minPoint = data[i];
			}
		}

		System.out.println("Min Distance = " + minDistance);
		for (int i = 0; i < minPoint.length; i++) {
			System.out.print(minPoint[i] + ", ");
		}
		System.out.println();
		return label;
	}

	private double distance(int row) {
		double sum = 0.0;
		for (int i = 0; i < testInstance.length; i++) {
			sum += Math.pow((this.data[row][i] - testInstance[i]), 2);
		}

		return Math.sqrt(sum);
	}
}
