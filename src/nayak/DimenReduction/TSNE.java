package nayak.DimenReduction;

import nayak.Utility.MatrixUtil;
import nayak.Utility.Print;
import Jama.Matrix;

/**
 * Implementation of t-distributed stochastic neighbor embedding
 * -based on open source c++ implementation on t-sne website
 * -only the part that computes the pairwise similarity matrix (still contains bugs)
 * 
 * Details:
 * http://en.wikipedia.org/wiki/T-distributed_stochastic_neighbor_embedding
 * 
 * @author Ashwin K Nayak
 *
 */
public class TSNE {

	double[][] data;
	double perplexity = 30.0;
	double theta = 0.5; // tradeoff between speed and accuracy, 1 = very crude approximations, 0 = standard, slow t-SNE
	double[] transformed;
	
	int numDimensions;
	int numDataPoints;
	int originalNumDimensions;

	// learning params
	double momentum = 0.5;
	double finalMomentum = 0.8;
	double eta = 200.0;
	boolean exact;
	int maxIterations = 1000;
	int stopLyingIter = 250;
	int momSwitchIter = 250;

	// alg data
	double[] dY;
	double[] uY;
	double[] gains;

	public TSNE(double[][] d, double p, double t, int numDims) {
		// Data should already be normalized (subtract means)
		data = d;
		perplexity = p;
		theta = t;

		numDimensions = numDims;
		numDataPoints = data.length;
		originalNumDimensions = data[0].length;

		exact = (theta == 0.0) ? true : false;
		
		transformed = new double[numDataPoints * numDims];
	}

	public void run() {
		dY = new double[numDataPoints * originalNumDimensions];
		uY = new double[numDataPoints * originalNumDimensions];
		gains = new double[numDataPoints * originalNumDimensions];
		for (int i = 0; i < numDataPoints * originalNumDimensions; i++)
			gains[i] = 1.0;
		
		// normalize
		zeroMean();
		double max = 0.0;
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[0].length; j++) {
				max = (data[i][j] > max) ? data[i][j] : max;
			}
		}
		for(int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[0].length; j++) {
				data[i][j] /= max;
			}
		}
		
		// compute input similarities
		double[] P = null;
		int[] rowP = null;
		int[] colP = null;
		double[] valP = null;

		if(exact) {
			P = new double[numDataPoints * numDataPoints];
			computeGaussianPerplexity(P); // FIND THE BUG
//			Print.print(P);
			// Symmetrize input similarities
			for(int n = 0; n < numDataPoints; n++) {
				for (int m = n + 1; m < numDataPoints; m++) {
					P[n * numDataPoints + m] += P[m * numDataPoints + n];
					P[m * numDataPoints + n] = P[n * numDataPoints + m];
				}
			}
			
			double sum_P = 0.0;
			for(int i = 0; i < numDataPoints*numDataPoints; i++) 
				sum_P += P[i];
			for(int i = 0; i < numDataPoints*numDataPoints; i++) 
				P[i] /= sum_P;
//			Print.print(P);
			Matrix PP = new Matrix(P, numDataPoints);
//			Print.print(PP);
		} else {
			// Compute input similarities for approximate t-SNE
		}
	}
	
	private void computeGaussianPerplexity(double[] P) {

		// Compute squared Euclidean distance matrix
		double[] DD = new double[numDataPoints * numDataPoints];
		computeSquaredEuclideanDistance(DD); // BUG
		Matrix asdf = new Matrix(DD, numDataPoints);
		Print.print(asdf);
//		Print.print(Tsnep.tsne_p(asdf.getArray(), 2));
		// Compute Gaussian kernel row by row
		for(int n = 0; n < numDataPoints; n++) {
			
			// Initialize some variables
			boolean found = false;
			double beta = 1.0;
			double min_beta = -1*Double.MAX_VALUE;
			double max_beta = Double.MAX_VALUE;
			double tol = 1e-5;
			double sum_P = 0.0;
			
			// Iterate until we found a good perplexity
			int iter = 0;
			while(!found && iter < 200) {
				// Compute Gaussian kernel row
				for(int m = 0; m < numDataPoints; m++)
					P[n * numDataPoints + m] = Math.exp(-beta * DD[n * numDataPoints + m]);
				P[n * numDataPoints + n] = Double.MIN_VALUE;
				
				// Compute entropy of current row
				sum_P = Double.MIN_VALUE;
				for(int m = 0; m < numDataPoints; m++)
					sum_P += P[n * numDataPoints + m];
				double H = 0.0;
				for(int m = 0; m < numDataPoints; m++)
					H += beta * (DD[n * numDataPoints + m] * P[n * numDataPoints + m]);
				H = (H / sum_P) + Math.log(sum_P);
				
				// Evaluate whether the entropy is within the tolerance level
				double Hdiff = H - Math.log(perplexity);
				if(Hdiff < tol && -Hdiff < tol) {
					found = true;
				} else {
					if(Hdiff > 0) {
						min_beta = beta;
						if(max_beta == Double.MAX_VALUE || max_beta == -Double.MAX_VALUE)
							beta *= 2.0;
						else
							beta = (beta + max_beta) / 2.0;
					} else {
						max_beta = beta;
						if(min_beta == -Double.MAX_VALUE || min_beta == Double.MAX_VALUE)
							beta /= 2.0;
						else
							beta = (beta + min_beta) / 2.0;
					}
				}
				
				// Update iteration counter
				iter++;
			}
			
			// Row normalize P
			for(int m = 0; m < numDataPoints; m++)
				P[n * numDataPoints + m] /= sum_P;
		}
	}
	
	private void computeSquaredEuclideanDistance(double[] DD) {
		double[] dataSums = new double[numDataPoints];
		
		for(int i = 0; i < numDataPoints; i++) {
			for(int d = 0; d < originalNumDimensions; d++) {
				double val = data[i][d];
				dataSums[i] += (val*val);
			}
		}
		
		for(int i = 0; i < numDataPoints; i++) {
			for(int m = 0; m < numDataPoints; m++) {
				DD[i * numDataPoints + m] = dataSums[i] + dataSums[m];
			}
		}
		
		// cblas_dgemm implementation (hopefully)
		// C = alpha*AB + beta*C
		// alpha = -2.0, beta = 1.0
		
		Matrix m = new Matrix(data);
//		Print.print(m);
		double[] aData = m.getColumnPackedCopy();
		Matrix B = new Matrix(aData, originalNumDimensions);
//		Print.print(B);
		Matrix A = B.transpose();
//		Print.print(A);
//		Print.print(A.times(B));
		Matrix C = new Matrix(DD, numDataPoints);
//		Print.print(C);
		C = (A.times(B).times(-2.0)).plus(C);
//		Print.print(C);
		
		DD = C.getColumnPackedCopy();
	}
	
	private void zeroMean() {
		// Compute data mean
		double[] mean = new double[originalNumDimensions];
		for(int n = 0; n < numDataPoints; n++) {
			for(int d = 0; d < originalNumDimensions; d++) {
				mean[d] += data[n][d];
			}
		}
		
		for(int d = 0; d < originalNumDimensions; d++) {
			mean[d] /= numDataPoints;
		}
		
		// Subtract data mean
		for(int n = 0; n < numDataPoints; n++) {
			for(int d = 0; d < originalNumDimensions; d++) {
				data[n][d] -= mean[d];
			}
		}
	}
	
	public static void main(String[] args) {
		double[][] d = { {1, 2, 3, 4}, {1, 2, 3, 4}, {20, 21, 22, 23}, {20.1, 21.2, 23.3, 24.6}};
		TSNE tsne = new TSNE(d, 30, 0.0, 2);
		tsne.run();
	}
}
