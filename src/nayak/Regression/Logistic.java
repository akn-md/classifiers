package nayak.Regression;

import java.io.Serializable;

import nayak.Abstract.Classifier;
import nayak.Abstract.Regression;
import nayak.Data.Crossvalidation;
import Jama.Matrix;

/**
 * Binary Logistic Regression. (1/(1+e^-x))
 * 
 * Features:
 * -Log cost function
 * 		-(-log(squared error) if y=1)
 * 		-(-log(1-squared error) if y=0)
 * 		-Unlike linear regression's squared error cost function (1/2(actual-predicted)^2), 
 * 		this is convex when used with the logistic function (no local optima)
 * -batch gradient descent
 * -regularization for batch gradient descent
 * 
 * To Add:
 * -different cost functions
 * -conjugate gradient, BFGS, L-BFGS
 * 
 * @author Ashwin K Nayak
 *
 */
public class Logistic extends Regression implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2305691582525372686L;

	boolean debug = false;

	public static void main(String[] args) {
		double[][] data = { { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 2.0 }, { 1.0, 0.0, -1.0 }, { 1.0, 0.0, -2.0 } };
		double[] labels = { 0, 0, 1, 1 };

//		Crossvalidation cv = new Crossvalidation(data, labels, 1123);
//		cv.generateRandomSets(0.5);
//		Logistic l = new Logistic(cv.getTrainingSet(), cv.getTrainingLabels(), false, true);
//		l.train(100);
//		l.printOutput();
//		System.out.println(l.getError(l.getPredictions(l.getData()), l.getLabels()));
//		Matrix testing = new Matrix(cv.getTestingSet());
//		Matrix testingL = new Matrix(cv.getTestingLabels(), cv.getTestingLabels().length);
//		Matrix predictions = l.getPredictions(testing);
//		l.print(predictions);
//		System.out.println(l.getError(predictions, testingL));

		//		l.printOutput();
		//		double[] d = { 1.0, 2.0, 10 };
		//		System.out.println(l.predict(d));
		//		l.printWeights();
	}

	public Logistic(boolean ualr, boolean rw) {
		useAdaptiveLearningRate = ualr;
		regularizeWeights = rw;
	}
	
	/**
	 * Cost(h(x), y) = -y*log(h(x)) - (1-y)*log(1-(h(x)) 
	 */
	@Override
	public double getError(Matrix data, Matrix labels) {
		Matrix predictions = getPredictions(data);
		
		double error = 0.0;

		double replaceZeroWith = Double.MIN_VALUE;
		double replaceOneWith = 1 - replaceZeroWith;

		for (int i = 0; i < predictions.getRowDimension(); i++) {
			double actual = labels.get(i, 0);
			double predicted = predictions.get(i, 0);

			if (predicted == 1.0)
				predicted = replaceOneWith;
			if (predicted == 0.0)
				predicted = replaceZeroWith;

			error += -1 * actual * Math.log(predicted) - (1 - actual) * Math.log(1 - predicted);
		}

		error /= predictions.getRowDimension();

		return error;
	}

	/**
	 * h(x) = p(y=1|x;theta) = 1/(1+e^-(theta*x))
	 */
	@Override
	protected Matrix getPredictions(Matrix data) {
		Matrix m = data.times(weights);
		for (int i = 0; i < m.getRowDimension(); i++) {
			double exp = 1 / (1 + Math.exp(-1 * m.get(i, 0)));
			m.set(i, 0, exp);
		}

		return m;
	}

	public double predict(double[] data) {
		double thetaX = 0.0;
		for (int i = 0; i < data.length; i++) {
			thetaX += (weights.get(i, 0) * data[i]);
		}

		return 1 / (1 + Math.exp(-1 * thetaX));
	}

	/**
	 * theta_new = theta_old - alpha*(Summation of (h(x)-y)*x)
	 */
	@Override
	protected void updateTheta() {
		//				print(dataMatrix);
		Matrix diffMatrix = getPredictions(data).minus(labels);
		//				print(diffMatrix);
		double aOverM = learningRate / data.getRowDimension();
		Matrix gradient = (data.transpose().times(diffMatrix)).times(aOverM);
		//						print(gradient);
		//				print(thetaMatrix);
		if (regularizeWeights) {
			double interceptWeight = weights.get(0, 0); // don't perform regularization on intercept weight
			double multiplier = 1 - learningRate * (regularizationCoefficient / data.getRowDimension());
			weights = weights.times(multiplier);
			weights.set(0, 0, interceptWeight);
		}
		weights = weights.minus(gradient);
		//				print(thetaMatrix);
	}
}
