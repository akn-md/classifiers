package nayak.NearestNeighbor;

import java.util.Random;

import nayak.Abstract.Classifier;
import nayak.Utility.Print;
import Jama.Matrix;

/**
 * Nearest Neighbor with a unique implementation of locality-sensitive hashing
 * 
 * Features:
 * -binary classification
 * -fast but very approximate dimensionality reduction
 * -fast prediction time
 * 
 * @author Ashwin K Nayak
 *
 */
public class rNN extends Classifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345172802320196086L;

	public static void main(String[] args) {
		double[][] data = { { 0.9, 0.2 },{ 0.8, 0.9 } };
		double[] labels = { 1,   0 };
		rNN rnn = new rNN(10, 10, 1123);
		rnn.init(new Matrix(data), new Matrix(labels, labels.length));
		rnn.train(null);
		System.out.println(rnn.getTrainingError());
		//		rnn.getPredictions(new Matrix(data));
	}

	int numFeatures;
	int tableSize;
	int numPatterns;
	int[][] randomNumberTables;
	int[] positiveCounts, negativeCounts;
	long seed;

	public rNN(int numPatterns, int tableSize, long seed) {
		this.numPatterns = numPatterns;
		this.tableSize = tableSize;
		this.seed = seed;

		positiveCounts = new int[numPatterns];
		negativeCounts = new int[numPatterns];
	}

	@Override
	public void init(Matrix data, Matrix labels) {
		// TODO Auto-generated method stub
		this.data = data;
		this.labels = labels;

		randomNumberTables = new int[data.getColumnDimension()][tableSize];
		initializeRNTs();
	}

	private void initializeRNTs() {
		Random random = new Random(seed);
		for (int i = 0; i < randomNumberTables.length; i++) {
			for (int j = 0; j < randomNumberTables[0].length; j++) {
				int val = random.nextInt();
				randomNumberTables[i][j] = val;
			}
		}
	}

	@Override
	public void train(double[] params) {
		// TODO Auto-generated method stub
		for (int n = 0; n < data.getRowDimension(); n++) {
			Matrix m = data.getMatrix(n, n, 0, data.getColumnDimension() - 1);
			int hash = calculateHash(m);

			double label = labels.get(n, 0);

			if (label == 1.0)
				positiveCounts[hash]++;
			else
				negativeCounts[hash]++;
		}
		
//		Print.print(positiveCounts);
//		Print.print(negativeCounts);
	}

	private int calculateHash(Matrix data) {
		int hash = 0;
		for (int i = 0; i < data.getColumnDimension(); i++) {
			int index = (int) (data.get(0, i) * tableSize);

			if (index == tableSize)
				index--;
			hash += randomNumberTables[i][index];
		}

		hash = hash % numPatterns;

		if (hash < 0)
			hash *= -1;

		return hash;
	}

	@Override
	protected Matrix getPredictions(Matrix data) {
		// TODO Auto-generated method stub
		Matrix predictions = new Matrix(data.getRowDimension(), 1);
		for(int n = 0; n < data.getRowDimension(); n++) {
			Matrix m = data.getMatrix(n, n, 0, data.getColumnDimension() - 1);
			int hash = calculateHash(m);
			
			int pos = positiveCounts[hash];
			int neg = negativeCounts[hash];
			double prediction = ((double) pos) / (pos + neg);
			predictions.set(n, 0, prediction);
		}
		
		return predictions;
	}

	@Override
	public double getError(Matrix data, Matrix labels) {
		// TODO Auto-generated method stub
		Matrix predictions = getPredictions(data);
		
		double error = 0.0;
		for(int n = 0; n < data.getRowDimension(); n++) {
			double predicted = predictions.get(n, 0);
			double actual = labels.get(n, 0);
			
			error += Math.pow((actual - predicted), 2);	
		}
		
		return error /= data.getRowDimension();
	}
}
