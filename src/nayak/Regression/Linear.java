package nayak.Regression;

import java.io.Serializable;

import nayak.Abstract.Classifier;
import nayak.Abstract.Regression;
import nayak.Optimization.Crossvalidation;
import Jama.Matrix;

/**
 * Multivariate Linear Regression
 * 
 * Features:
 * -Least-squares cost function
 * -Normal equation method (use if n < 1000)
 * -Regularization for normal equation method
 * 
 * To Add:
 * -calculate cost differently if using regularization (add regularization term)
 * -batch gradient descent
 * -regularization for gradient descent (theta = theta_old - alpha*(1/m)*Summation(predicted-actual)*value + (lambda/m)*theta_old) 
 * -locally weighted linear regression
 * 
 * @author Ashwin K Nayak
 *
 */
public class Linear extends Regression implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6511317479353788529L;

	public static void main(String[] args) {
		double[][] data = { { 1, 2104, 5, 1, 45 }, { 1, 1416, 3, 2, 40 }, { 1, 1534, 3, 2, 30 }, { 1, 852, 2, 1, 36 } };
		double[] labels = { 460, 232, 315, 178 };
		
//		Crossvalidation cv = new Crossvalidation(data, labels, 1123);
//		cv.generateRandomSets(0.5);
//		Linear l = new Linear(cv.getTrainingSet(), cv.getValidationSet(), cv.getTestingSet(), cv.getTrainingLabels(),
//				cv.getValidationLabels(), cv.getTestingLabels(), false, true);
//		l.solveNormalEquation();
//		l.printOutput(Classifier.TRAINING);
//		l.getError(Classifier.TRAINING);
//		l.printOutput(Classifier.VALIDATION);
//		l.getError(Classifier.VALIDATION);
//		l.printOutput(Classifier.TESTING);
//		l.getError(Classifier.TESTING);
//		l.printWeights();
//		l.printEquation();

		//				double[] d = { 1.0, 2.0, 10 };
		//				System.out.println(l.predict(d));
	}

	public Linear(boolean ualr, boolean rw) {
		useAdaptiveLearningRate = ualr;
		regularizeWeights = rw;
	}

	@Override
	protected void updateTheta() {

	}

	/**
	 * weights = (X'*X)^-1*X'y
	 */
	public void solveNormalEquation() {
		Matrix m;
		if (regularizeWeights) {
			// resulting matrix m will NOT be singular because of regularization coefficient
			Matrix identity = Matrix.identity(weights.getRowDimension(), weights.getRowDimension());
			identity.set(0, 0, 0.0);
			identity.times(regularizationCoefficient);
			m = ((data.transpose().times(data)).plus(identity)).inverse();
		} else {
			m = (data.transpose().times(data)).inverse();
		}

		Matrix m1 = data.transpose().times(data);
		weights = m.times(m1);
	}

	@Override
	protected Matrix getPredictions(Matrix data) {
		return data.times(weights);
	}

	@Override
	public double predict(double[] data) {
		double thetaX = 0.0;
		for (int i = 0; i < data.length; i++) {
			thetaX += (weights.get(i, 0) * data[i]);
		}

		return thetaX;
	}

	/**
	 * Least squares cost function.
	 * Average error = (1/m)*Summation((actual-predicted)^2/2)
	 */
	@Override
	public double getError(Matrix data, Matrix labels) {
		Matrix predictions = getPredictions(data);
		
		Matrix diff = labels.minus(predictions);
		diff = diff.arrayTimes(diff);
		diff = diff.times(0.5);

		Matrix ones = new Matrix(diff.getRowDimension(), 1, 1);
		double error = diff.times(ones.transpose()).get(0, 0);

		error /= diff.getRowDimension();
		System.out.println("Error = " + error);

		return error;
	}
}
