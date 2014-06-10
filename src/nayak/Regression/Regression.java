package nayak.Regression;

import java.beans.FeatureDescriptor;
import java.io.Serializable;

import Jama.Matrix;

/**
 * Abstract Regression class
 * 
 * Features:
 * -matrix operations
 * 
 * To Add:
 * 
 * @author Ashwin
 *
 */
public abstract class Regression implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5903144327657084197L;
	
	protected Matrix data, weights, labels;
	protected double learningRate; // higher the learning rate, faster the convergence
	protected double annealingRate; // higher the annealing rate, slower the learning rate reduces, faster the convergence
	protected boolean useAdaptiveLearningRate;
	
	//////////////////////////
	//// Abstract Methods ////
	//////////////////////////
	abstract protected void updateTheta();
	abstract protected double calculateCost(int row, Matrix predictions);
	abstract protected Matrix getPredictions();
	abstract public double predict(double[] data);
	
	/**
	 * Initializes data, weight, and label matrices. Adds ones to data.
	 * @param data
	 */
	protected void init(double[][] data, double[] labels) {
		this.data = new Matrix(data);
		
		// initialize weights
		double[] dd = new double[this.data.getColumnDimension()];
		weights = new Matrix(dd, dd.length);

		// initialize labels
		this.labels = new Matrix(labels, labels.length);
	}
	
	/**
	 * Trains regression algorithm for specified number of iterations.
	 * @param numIterations
	 */
	public void train(int numIterations) {
		for (int i = 0; i < numIterations; i++) {
			updateTheta();
			System.out.println(calculateOverallCost());
		}

	}
	
	/**
	 * Calculates overall (average) cost of predictions made using current weight matrix.
	 * @return
	 */
	public double calculateOverallCost() {
		Matrix m = getPredictions();
		double cost = 0.0;

		for (int i = 0; i < data.getRowDimension(); i++) {
			cost += calculateCost(i, m);
		}

		return cost / data.getRowDimension();
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
		Matrix output = getPredictions();
		for (int i = 0; i < data.getRowDimension(); i++) {
			System.out.println("Predicted = " + output.get(i, 0) + ", Actual = " + labels.get(i, 0) + ", Cost = "
					+ calculateCost(i, output));
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
