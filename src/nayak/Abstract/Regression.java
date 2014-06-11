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
	protected double learningRate = 1.0; // higher the learning rate, faster the convergence
	protected double annealingRate = 1.0; // higher the annealing rate, slower the learning rate reduces, faster the convergence
	protected boolean useAdaptiveLearningRate, regularizeWeights, warning;
	protected double regularizationCoefficient = 1.0;

	//////////////////////////
	//// Abstract Methods ////
	//////////////////////////
	abstract protected void updateTheta();

	abstract protected double getError(Matrix predictions, Matrix labels);

	abstract public double predict(double[] data);

	/**
	 * Initializes data, weight, and label matrices.
	 * @param data
	 */
	protected void init(double[][] training, double[][] validation, double[][] testing, double[] trainLabels,
			double[] validateLabels, double[] testLabels, boolean ualr, boolean rw) {

		trainingData = new Matrix(training);
		validationData = new Matrix(validation);
		testingData = new Matrix(testing);

		trainingLabels = new Matrix(trainLabels, trainLabels.length);
		validationLabels = new Matrix(validateLabels, validateLabels.length);
		testingLabels = new Matrix(testLabels, testLabels.length);

		useAdaptiveLearningRate = ualr;
		regularizeWeights = rw;

		// initialize weights to matrix of zeros
		weights = new Matrix(trainingData.getColumnDimension(), 1);

		if (regularizeWeights) {
			double multiplier = 1 - learningRate * (regularizationCoefficient / trainingData.getRowDimension());
			if (multiplier < 0) {
				System.err.println("Warning! Regularization multiplier is less than 0! ");
				warning = true;
			}
		}
	}

	@Override
	public void resetTraining(double[][] data, double[] labels) {
		trainingData = new Matrix(data);
		trainingLabels = new Matrix(labels, labels.length);
	}
	
	/**
	 * Trains regression algorithm for specified number of iterations.
	 * @param numIterations
	 */
	public void train(int numIterations) {
		if (warning) {
			System.err.println("Decrease learning rate or regularization coefficient or "
					+ "increase number of training examples!");
		}
		for (int i = 0; i < numIterations; i++) {
			updateTheta();
		}

	}

	@Override
	public void train(double[] params) {
		train((int) params[0]);
	}
	
	public double getError(int type) {
		Matrix m = getPredictions(type);
		Matrix l = getLabels(type);

		return getError(m, l);
	}

	protected Matrix getLabels(int type) {
		switch (type) {
		case Classifier.TRAINING:
			return trainingLabels;
		case Classifier.TESTING:
			return testingLabels;
		case Classifier.VALIDATION:
			return validationLabels;
		default:
			return null;
		}
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

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getAnnealingRate() {
		return annealingRate;
	}

	public void setAnnealingRate(double annealingRate) {
		this.annealingRate = annealingRate;
	}

	public double getRegularizationCoefficient() {
		return regularizationCoefficient;
	}

	public void setRegularizationCoefficient(double regularizationCoefficient) {
		this.regularizationCoefficient = regularizationCoefficient;
	}

	public void printWeights() {
		print(weights);
	}

	public void printOutput(int type) {
		Matrix output = getPredictions(type);
		Matrix labels = getLabels(type);
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
