package nayak.Regression;

import nayak.Regression.Regression;
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
 * -adaptive learning rate
 * 		new rate = old rate/(1+e/annealing rate)
 * -batch gradient descent
 * 
 * To Add:
 * -support for multiple classes
 * -different cost functions
 * -support for nonlinear boundaries
 * -conjugate gradient, BFGS, L-BFGS
 * 
 * @author Ashwin
 *
 */
public class Logistic extends Regression {

	boolean debug = false;

	public static void main(String[] args) {
		double[][] data = { { 0.0, 1.0 }, { 0.0, 2.0 }, { 0.0, -1.0 }, { 0.0, -2.0 } };
		double[] labels = { 0, 0, 1, 1 };
		Logistic l = new Logistic(data, labels);
		l.printOutput();
		System.out.println(l.calculateOverallCost());

		l.train(100);
		l.printOutput();
		l.printEquation();
		double[] d = { 1.0, 2.0, 10 };
		System.out.println(l.predict(d));
	}

	public Logistic(double[][] data, double[] labels) {
		super.init(data, labels, true);
		learningRate = 10.0;
		useAdaptiveLearningRate = false;
		print(this.data);
	}

	public Logistic(double[][] data, double[] labels, double lr) {
		super.init(data, labels, true);
		learningRate = lr;
		useAdaptiveLearningRate = false;
	}

	public Logistic(double[][] data, double[] labels, double lr, double ar) {
		super.init(data, labels, true);
		learningRate = lr;
		useAdaptiveLearningRate = true;
		annealingRate = ar;
	}

	/**
	 * Cost(h(x), y) = -y*log(h(x)) - (1-y)*log(1-(h(x)) 
	 * -easier way to write cost function
	 */
	protected double calculateCost(int row, Matrix output) {
		double actual = labels.get(row, 0);
		double predicted = output.get(row, 0);

		if (predicted == 1.0)
			predicted = 0.9999;

		if (predicted == 0.0)
			predicted = 0.0001;

		if (actual == 0.0) {
			return (-1 * Math.log10(1 - predicted));
		} else {
			return (-1 * Math.log10(predicted));
		}
	}

	/**
	 * h(x) = p(y=1|x;theta) = 1/(1+e^-(theta*x))
	 */
	protected Matrix getPredictions() {
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
	//	private void updateThetaOld() {
	//		for (int i = 0; i < theta.length; i++) {
	//			double gradient = calculateGradient(i);
	//			if (debug)
	//				System.out.println("Gradient for theta " + i + " = " + gradient);
	//			if (useAdaptiveLearningRate)
	//				learningRate = calculateLearningRate(learningRate);
	//			theta[i] -= (learningRate * gradient);
	//			if (debug)
	//				System.out.println("Theta = " + theta[i]);
	//		}
	//	}

	/**
	 * Using Matrices
	 */
	protected void updateTheta() {
		//				print(dataMatrix);
		Matrix diffMatrix = getPredictions().minus(labels);
		//				print(diffMatrix);
		Matrix gradient = data.transpose().times(diffMatrix);
		//				print(gradient);
		//				print(thetaMatrix);
		weights = weights.minus(gradient.times(learningRate / data.getRowDimension()));
		//				print(thetaMatrix);
	}

	//	private double calculateGradient(int col) {
	//		double gradient = 0.0;
	//		for (int i = 0; i < data.length; i++) {
	//			gradient += ((getOutput(i) - labels[i]) * data[i][col]);
	//		}
	//		return gradient / data.length;
	//	}

	//	private void printTheta() {
	//		for (int i = 0; i < theta.length; i++) {
	//			System.out.println("theta[" + i + "] = " + theta[i]);
	//		}
	//	}
}
