package nayak.Regression;

import java.io.Serializable;

import Jama.Matrix;

/**
 * Multivariate Linear Regression
 * 
 * Features:
 * -Least-squares cost function
 * -Normal equation method (use if n < 1000)
 * 
 * To Add:
 * 
 * 
 * @author Ashwin
 *
 */
public class Linear extends Regression implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6511317479353788529L;

	public static void main(String[] args) {
		double[][] data = { { 2104, 5, 1, 45 }, { 1416, 3, 2, 40 }, { 1534, 3, 2, 30 }, { 852, 2, 1, 36 } };
		double[] labels = { 460, 232, 315, 178 };
		Linear l = new Linear(data, labels);
		//		l.printOutput();
		System.out.println(l.calculateOverallCost());

		l.train(1);
		System.out.println(l.calculateOverallCost());
//		l.printOutput();
//		l.printEquation();
//		double[] d = { 1.0, 2.0, 10 };
//		System.out.println(l.predict(d));
	}
	
	public Linear(double[][] data, double[] labels) {
		super.init(data, labels);
	}

	
	@Override
	protected void updateTheta() {
		
	}

	/**
	 * weights = (X'*X)^-1*X'y
	 */
	public void solveNormalEquation() {
		print(weights);
		// TODO Auto-generated method stub
		Matrix m = (data.transpose().times(data)).inverse();
//		print(m);
		Matrix m1 = data.transpose().times(labels);
//		print(m1);
		weights = m.times(m1);
		print(weights);
	}
	
	@Override
	protected double calculateCost(int row, Matrix predictions) {
		double actual = labels.get(row, 0);
		double predicted = predictions.get(row, 0);

		System.out.println(actual + "," + predicted);

		return Math.pow((actual - predicted), 2)/2;
	}

	@Override
	protected Matrix getPredictions() {
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
}
