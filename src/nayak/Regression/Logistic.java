package nayak.Regression;
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
 * 
 * To Add:
 * -support for multiple classes
 * -different cost functions
 * -support for nonlinear boundaries
 * -feature scaling and centering
 * -conjugate gradient, BFGS, L-BFGS
 * 
 * @author Ashwin
 *
 */
public class Logistic {

	boolean debug = false;
	boolean useAdaptiveLearningRate = false;

	public static void main(String[] args) {
	}

	// params
	double learningRate = 10; // higher the learning rate, faster the convergence
	double annealingRate = 1000000; // higher the annealing rate, slower the learning rate reduces, faster the convergence

	// new implementation
	Matrix dataMatrix, thetaMatrix, labelMatrix;

	public Logistic(double[][] data, double[] labels) {
		labelMatrix = new Matrix(labels, labels.length);

		init(data);
	}

	public void train(int numIterations) {
		for (int i = 0; i < numIterations; i++) {
			updateTheta();
			System.out.println(calculateOverallCost());
		}

	}

	private void init(double[][] data) {
		initializeData(data);
		initializeWeights();
	}

	private void initializeData(double[][] data) {
		double[][] d = new double[data.length][data[0].length + 1];
		for (int i = 0; i < data.length; i++) {
			d[i][0] = 1.0;
			System.arraycopy(data[i], 0, d[i], 1, data[i].length);
		}

		dataMatrix = new Matrix(d);
	}

	private void initializeWeights() {
		double[] d = new double[dataMatrix.getColumnDimension()];
		thetaMatrix = new Matrix(d, d.length);
	}

	/**
	 * Cost(h(x), y) = -y*log(h(x)) - (1-y)*log(1-(h(x)) 
	 * -easier way to write cost function
	 */
	private double calculateCost(int row, Matrix output) {
		double actual = labelMatrix.get(row, 0);
		double predicted = output.get(row, 0);

		if (predicted == 1.0)
			predicted = 0.9999;

		if (predicted == 0.0)
			predicted = 0.0001;

		if (actual == 0.0) {
			return -1 * Math.log10(1 - predicted);
		} else {
			return -1 * Math.log10(predicted);
		}
		//		return (-1 * actual) * Math.log10(predicted) - (1 - actual) * Math.log10(1 - predicted);
	}

	public double calculateOverallCost() {
		Matrix m = getOutput();
		double cost = 0.0;

		for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
			cost += calculateCost(i, m);
		}

		return cost / dataMatrix.getRowDimension();
	}

	/**
	 * h(x) = p(y=1|x;theta) = 1/(1+e^-(theta*x))
	 */
	private Matrix getOutput() {
		Matrix m = dataMatrix.times(thetaMatrix);
		for (int i = 0; i < m.getRowDimension(); i++) {
			double exp = 1 / (1 + Math.exp(-1 * m.get(i, 0)));
			m.set(i, 0, exp);
		}

		return m;
	}

	public double predict(double[] data) {
		double thetaX = 0.0;
		for (int i = 0; i < data.length; i++) {
			thetaX += (thetaMatrix.get(i, 0) * data[i]);
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
	private void updateTheta() {
		//				print(dataMatrix);
		Matrix diffMatrix = getOutput().minus(labelMatrix);
		//				print(diffMatrix);
		Matrix gradient = dataMatrix.transpose().times(diffMatrix);
		//				print(gradient);
		//				print(thetaMatrix);
		thetaMatrix = thetaMatrix.minus(gradient.times(learningRate / dataMatrix.getRowDimension()));
		//				print(thetaMatrix);
	}

//	private double calculateGradient(int col) {
//		double gradient = 0.0;
//		for (int i = 0; i < data.length; i++) {
//			gradient += ((getOutput(i) - labels[i]) * data[i][col]);
//		}
//		return gradient / data.length;
//	}

	/**
	 * new rate = old rate/(1+e/annealing rate)
	 * @param old learning rate
	 * @return
	 */
	private double calculateLearningRate(double old) {
		return old / (1 + Math.E / annealingRate);
	}

//	private void printTheta() {
//		for (int i = 0; i < theta.length; i++) {
//			System.out.println("theta[" + i + "] = " + theta[i]);
//		}
//	}

	public void printOutput() {
		Matrix output = getOutput();
		for (int i = 0; i < dataMatrix.getRowDimension(); i++) {
			System.out.println("Predicted = " + output.get(i, 0) + ", Actual = " + labelMatrix.get(i, 0) + ", Cost = "
					+ calculateCost(i, output));
		}
	}

	public void printEquation() {
		System.out.print("y = " + thetaMatrix.get(0, 0));
		for (int i = 1; i < thetaMatrix.getRowDimension(); i++) {
			System.out.print(" + " + thetaMatrix.get(i, 0) + "*X" + i);
		}
		System.out.println();
	}

	public void print(Matrix m) {
		for (int i = 0; i < m.getRowDimension(); i++) {
			System.out.print("Row " + i + "\t");
			for (int j = 0; j < m.getColumnDimension(); j++) {
				System.out.print(m.get(i, j) + "\t");
			}
			System.out.println();
		}
	}
}
