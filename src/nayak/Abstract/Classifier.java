package nayak.Abstract;

import java.io.Serializable;

import Jama.Matrix;

/**
 * Abstract class for a classifier.
 * 
 * All classifiers have the following in common:
 * -initialization
 * -training
 * -getting predictions and error
 * 
 * @author Ashwin K Nayak
 *
 */
public abstract class Classifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5332343689449907770L;

//	public static final int TRAINING = 0;
//	public static final int VALIDATION = 1;
//	public static final int TESTING = 2;

	protected Matrix data, labels;

	abstract public void init(Matrix data, Matrix labels);
	
	abstract public void train(double[] params);

	abstract protected Matrix getPredictions(Matrix data);
	
	abstract public double getError(Matrix data, Matrix labels);
		
	public double getTrainingError() {
		return getError(data, labels);
	}
	
	public Matrix getData() {
		return data;
	}
	
	public Matrix getLabels() {
		return labels;
	}
	
	public void print(Matrix m) {
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
