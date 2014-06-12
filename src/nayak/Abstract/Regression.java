package nayak.Abstract;

import java.io.Serializable;

import Jama.Matrix;

/**
 * Abstract Regression class
 * 
 * Features:
 * -matrix operations
 * -adaptive learning rate
 * 		new rate = old rate/(1+e/annealing rate)
 * 
 * To Add:
 * -support for multiple classes
 * -support for nonlinear boundaries
 * -locally weighted predictions
 * 
 * @author Ashwin K Nayak
 *
 */
public abstract class Regression extends Classifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5903144327657084197L;

	protected Matrix weights;
	public double learningRate = 1.0; // higher the learning rate, faster the convergence
	public double annealingRate = 1.0; // higher the annealing rate, slower the learning rate reduces, faster the convergence
	public double regularizationCoefficient = 1.0;
	protected boolean useAdaptiveLearningRate, regularizeWeights, warning;

	//////////////////////////
	//// Abstract Methods ////
	//////////////////////////
	abstract protected void updateTheta();

	abstract public double predict(double[] data);

	/**
	 * Initializes data, weight, and label matrices.
	 * @param data
	 */
	@Override
	public void init(double[][] data, double[] labels) {

		this.data = new Matrix(data);
		this.labels = new Matrix(labels, labels.length);

		// initialize weights to matrix of zeros
		weights = new Matrix(this.data.getColumnDimension(), 1);

		if (regularizeWeights) {
			double multiplier = 1 - learningRate * (regularizationCoefficient / this.data.getRowDimension());
			if (multiplier < 0) {
				System.err.println("Warning! Regularization multiplier is less than 0! ");
				warning = true;
			}
		}
	}

	/**
	 * Trains regression algorithm for specified number of iterations.
	 * @param numIterations
	 */
	public void train(int numIterations) {
		if (warning) {
			System.err.println("Decrease learning rate or regularization coefficient or "
					+ "increase number of training examples!");
		} else {
			for (int i = 0; i < numIterations; i++) {
				updateTheta();
//				System.out.println(getError(data, labels));
			}
		}
	}

	@Override
	public void train(double[] params) {
		train((int) params[0]);
	}
	
	@Override
	public double getTrainingError() {
		return getError(data, labels);
	}

	/**
	 * new rate = old rate/(1+e/annealing rate)
	 * @param old learning rate
	 * @return
	 */
	protected double calculateLearningRate(double old) {
		return old / (1 + Math.E / annealingRate);
	}

	///////////////////////////
	///////// Helpers /////////
	///////////////////////////

	public void printWeights() {
		print(weights);
	}

	public void printOutput() {
		Matrix output = getPredictions(data);
		for (int i = 0; i < output.getRowDimension(); i++) {
			System.out.println("Predicted = " + output.get(i, 0) + ", Actual = " + labels.get(i, 0));
		}
	}

	public void printEquation() {
		System.out.print("y = " + weights.get(0, 0));
		for (int i = 1; i < weights.getRowDimension(); i++) {
			System.out.print(" + " + weights.get(i, 0) + "*X" + i);
		}
		System.out.println();
	}

	protected void print(Matrix m) {
		for (int i = 0; i < m.getRowDimension(); i++) {
			System.out.print("Row " + i + "\t");
			for (int j = 0; j < m.getColumnDimension(); j++) {
				System.out.print(m.get(i, j) + "\t");
			}
			System.out.println();
		}
	}
}
