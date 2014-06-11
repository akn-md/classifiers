package nayak.DimenReduction;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Steps for PCA (Reduction of n-dimensional data to m dimensions) 
 * 1) Create covariance matrix of n-dimensional data 
 * 2) Calculate eigenvalues and associated eigenvectors from covariance matrix 
 * 3) Sort eigenvectors by eigenvalue
 * 4) Construct feature vector from top m eigenvectors
 * 5) Mean-adjust original data
 * 6) Transpose feature vector and mean-adjusted data
 * 7) Multiply transposed matrices to transpose results to obtain final data
 * 
 * @author Ashwin K Nayak
 * 
 */
public class PCA {
	
	public static void main(String[] args) {
		
	}
	
	Matrix covMatrix;
	EigenvalueDecomposition eigenstuff;
	double[] eigenvalues;
	Matrix eigenvectors;
	SortedSet<PrincipleComponent> principleComponents;
	double[] means;
	double[][] adjustedInput;
	
	public PCA(double[][] input) {
		// calculate covariance matrix
		means = new double[input[0].length];
		getCovariance(input);
		
		// gen eigenvalues
		eigenstuff = covMatrix.eig();
		eigenvalues = eigenstuff.getRealEigenvalues();
		
		// get eigenvectors
		eigenvectors = eigenstuff.getV();
		double[][] vecs = eigenvectors.getArray();
		
		// add principal components to tree
		int numComponents = eigenvectors.getColumnDimension(); 
		principleComponents = new TreeSet<PrincipleComponent>();
		for (int i = 0; i < numComponents; i++) {
			double[] eigenvector = new double[numComponents];
			for (int j = 0; j < numComponents; j++) {
				eigenvector[j] = vecs[i][j];
			}
			principleComponents.add(new PrincipleComponent(eigenvalues[i], eigenvector));
		}
		
		adjustedInput = getMeanAdjusted(input, means);
	}
	
	/**
	 * Returns transformed data based on the top k eigenvectors/values
	 * @param k
	 * @return
	 */
	public double[][] transform(int k, boolean print) {
		List<PrincipleComponent> dom = getDominantComponents(k);
		
		// get feature vector
		Matrix featureTranspose = getDominantComponentsMatrix(dom).transpose();
		Matrix inputTranspose = new Matrix(adjustedInput).transpose();
		
		// Final data = rowFeatureVector x rowDataAdjust
		Matrix output = featureTranspose.times(inputTranspose);
		double[][] ret = output.transpose().getArray();
		
		if(print) {
			for(int i = 0; i < ret.length; i++) {
				for(int j = 0; j < ret[0].length; j++) {
					System.out.print(ret[i][j] + ",");
				}
				System.out.print("\n");	
			}
		}
		
		return ret;
	}
	
	/**
	 * Transforms testing data.
	 * @param k
	 * @param testingData
	 * @return
	 */
	public double[][] transform(int k, double[][] testingData) {
		double[][] adjustedTesting = getMeanAdjusted(testingData, means);
		
		List<PrincipleComponent> dom = getDominantComponents(k);

		Matrix featureTranspose = getDominantComponentsMatrix(dom).transpose();
		Matrix inputTranspose = new Matrix(adjustedTesting).transpose();
		
		// Final data = rowFeatureVector x rowDataAdjust
		Matrix output = featureTranspose.times(inputTranspose);
		return output.transpose().getArray();
	}
	public void printCovMatrix() {
		for(int i = 0; i < covMatrix.getRowDimension(); i++) {
			for(int j = 0; j < covMatrix.getColumnDimension(); j++) {
				System.out.print(covMatrix.get(i, j) + ",");
			}
			System.out.print("\n");	
		}
	}
	
	public void printEigenValues() {
		for(double d:eigenvalues) {
			System.out.println(d);
		}
	}
	
	public void printEigenVectors() {
		double[][] vecs = eigenvectors.getArray();
		for(int i = 0; i < vecs.length; i++) {
			for(int j = 0; j < vecs[0].length; j++) {
				System.out.print(vecs[i][j] + ",");
			}
			System.out.print("\n");	
		}
	}

	private double[][] getMeanAdjusted(double[][] input, double[] mean) {
		int nRows = input.length;
		int nCols = input[0].length;
		double[][] ret = new double[nRows][nCols];
		for (int row = 0; row < nRows; row++) {
			for (int col = 0; col < nCols; col++) {
				ret[row][col] = input[row][col] - mean[col];
			}
		}
		return ret;
	}

	/**
	 * Returns the top n principle components in descending order of relevance.
	 */
	public List<PrincipleComponent> getDominantComponents(int n) {
		List<PrincipleComponent> ret = new ArrayList<PrincipleComponent>();
		int count = 0;
		for (PrincipleComponent pc : principleComponents) {
			ret.add(pc);
			count++;
			if (count >= n) {
				break;
			}
		}
		return ret;
	}

	public Matrix getDominantComponentsMatrix(List<PrincipleComponent> dom) {
		int nRows = dom.get(0).eigenVector.length;
		int nCols = dom.size();
		Matrix matrix = new Matrix(nRows, nCols);
		for (int col = 0; col < nCols; col++) {
			for (int row = 0; row < nRows; row++) {
				matrix.set(row, col, dom.get(col).eigenVector[row]);
			}
		}
		return matrix;
	}

	public int getNumComponents() {
		return eigenvalues.length;
	}

	public void getCovariance(double[][] input) {
		int numDataVectors = input.length;
		int n = input[0].length;

		double[] sum = new double[n];
		double[] mean = new double[n];
		
		// calculate column sums
		for (int i = 0; i < numDataVectors; i++) {
			double[] vec = input[i];
			for (int j = 0; j < n; j++) {
				sum[j] = sum[j] + vec[j];
			}
		}
		
		// calculate column means
		for (int i = 0; i < sum.length; i++) {
			mean[i] = sum[i] / numDataVectors;
		}

		double[][] ret = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				double v = getCovariance(input, i, j, mean);
				ret[i][j] = v;
				ret[j][i] = v;
			}
		}
		if (means != null) {
			System.arraycopy(mean, 0, means, 0, mean.length);
		}
		
		covMatrix = new Matrix(ret);
	}

	/**
	 * Gives covariance between vectors in an n-dimensional space. The two input
	 * arrays store values with the mean already subtracted. Read the code.
	 */
	private double getCovariance(double[][] matrix, int colA, int colB, double[] mean) {
		double sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			double v1 = matrix[i][colA] - mean[colA];
			double v2 = matrix[i][colB] - mean[colB];
			sum = sum + (v1 * v2);
		}
		int n = matrix.length;
		double ret = (sum / (n - 1));
		return ret;
	}

	public static class PrincipleComponent implements Comparable<PrincipleComponent> {
		public double eigenValue;
		public double[] eigenVector;

		public PrincipleComponent(double eigenValue, double[] eigenVector) {
			this.eigenValue = eigenValue;
			this.eigenVector = eigenVector;
		}

		public int compareTo(PrincipleComponent o) {
			int ret = 0;
			if (eigenValue > o.eigenValue) {
				ret = -1;
			} else if (eigenValue < o.eigenValue) {
				ret = 1;
			}
			return ret;
		}
	}
}
