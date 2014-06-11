package nayak.Abstract;

import java.io.Serializable;

import Jama.Matrix;

/**
 * Abstract class for a classifier.
 * 
 * All classifiers have the following in common:
 * -training data
 * -validation data
 * -testing data
 * 
 * @author Ashwin K Nayak
 *
 */
public abstract class Classifier implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5332343689449907770L;

	public static final int TRAINING = 0;
	public static final int VALIDATION = 1;
	public static final int TESTING = 2;

	protected Matrix trainingData, validationData, testingData;
	protected Matrix trainingLabels, validationLabels, testingLabels;

	abstract public void train(double[] params);
	abstract public double getError(int type);
	abstract protected Matrix getPredictions(Matrix data);
	abstract public void resetTraining(double[][] data, double[] labels);
	
	protected Matrix getPredictions(int type) {
		switch (type) {
		case Classifier.TRAINING:
			return getPredictions(trainingData);
		case Classifier.TESTING:
			return getPredictions(testingData);
		case Classifier.VALIDATION:
			return getPredictions(validationData);
		default:
			return null;
		}
	}

}
