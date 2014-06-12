package nayak.Optimization;

import java.util.Arrays;
import java.util.Random;

import nayak.Abstract.Classifier;
import nayak.Data.Crossvalidation;
import nayak.Data.Preprocessor;

/**
 * Selects the best features.
 * 
 * Features:
 * -finds single best feature
 * -searches for best combination of features
 * -searches for best combination of polynomial features
 * 
 * To Add:
 * -built in CV and maybe learning curves
 * 
 * @author Ashwin K Nayak
 *
 */
public class FeatureSelector {

	Classifier c;
	Crossvalidation cv;

	double[][] allData;
	double[] allLabels;
	double[] trainingParams;

	public FeatureSelector(Classifier c, double[] params) {
		this.c = c;
		this.allData = c.getData().getArray();
		this.allLabels = c.getLabels().getColumnPackedCopy();
		cv = new Crossvalidation(allData, allLabels, 1123);
		trainingParams = params;
	}

	public void findSingleBestFeature() {
		double min = Integer.MAX_VALUE;
		int minFeature = -1;

		int[] cols = new int[1];
		for (int i = 0; i < allData[0].length; i++) {
			cols[0] = i;
			double[][] data = getSubset(cols);
			Preprocessor.addOnes(data);
			c.init(data, allLabels);
			c.train(trainingParams);
			double error = c.getTrainingError();
			System.out.println("Feature " + i + ", error = " + error);

			if (error < min) {
				min = error;
				minFeature = i;
			}
		}
		System.out.println("Best single feature index = " + minFeature + ", with error = " + min);
	}

	public void runFeatureFinder(int numIterations, long seed) {
		double min = Integer.MAX_VALUE;
		int[] bestFeatureSet = null;
		
		Random random = new Random(seed);

		int count = 0;
		while (count < numIterations) {
			System.out.println("-----------------------");
			// how many features?
			int numFeatures = (int) (random.nextDouble() * allData[0].length) + 1;
			System.out.println("Trying " + numFeatures + " features");
			System.out.print("Using indices ");

			int[] features = new int[numFeatures];
			boolean[] taken = new boolean[allData[0].length];
			int featureIndex = -1;
			for (int i = 0; i < features.length; i++) {
				do {
					featureIndex = (int) (random.nextDouble() * allData[0].length);
				} while(taken[featureIndex] == true);
				taken[featureIndex] = true;
				features[i] = featureIndex;
				System.out.print(featureIndex + ", ");
			}
			System.out.println();
			
			double[][] data = getSubset(features);
			Preprocessor.addOnes(data);
			c.init(data, allLabels);
			c.train(trainingParams);
			double error = c.getTrainingError();
			System.out.println("Error = " + error);

			if (error < min) {
				min = error;
				bestFeatureSet = features;
			}
			
			count++;
		}
		
		System.out.println("##########################");
		System.out.println("Best Feature Set = " + Arrays.toString(bestFeatureSet));
		System.out.println("Error = " + min);
	}
	
	public void runPolynomialFeatureFinder(int numIterations, long seed, int[] features, int degree) {
		double min = Integer.MAX_VALUE;
		String best = null;
		
		// data without terms
		double[][] data = getSubset(features);
		
		Random random = new Random(seed);
		int[] terms = new int[degree];
		
		int count = 0;
		while(count < numIterations) {
			String s = "";
			System.out.println("-----------------------");
			// How many polynomial terms?
			int numTerms = (int) (random.nextDouble() * features.length) + 1;
			System.out.println("Trying " + numTerms + " polynomial terms of degree = " + degree);
			
			double[][] adjustedData = addColumns(numTerms, data);
			
			for(int i = 0; i < numTerms; i++) {
				// choose terms
				for(int j = 0; j < degree; j++) {
					int feature = (int) (random.nextDouble() * features.length);
					terms[j] = feature;
					s += "X" + feature;
				}
				
//				System.out.println("Term " + i + " = " + Arrays.toString(terms));
				
				// transform data
				for(int row = 0; row < adjustedData.length; row++) {
					double value = 1;
					for(int term = 0; term < terms.length; term++) {
						value *= adjustedData[row][terms[term]];
					}
					adjustedData[row][data[0].length + i] = value;
				}
				
				s+= ", ";
			}
			System.out.println("Terms = " + s);
			Preprocessor.addOnes(adjustedData);
			c.init(adjustedData, allLabels);
			c.train(trainingParams);
			double error = c.getTrainingError();
			System.out.println("Error = " + error);
			
			if(error <  min) {
				min = error;
				best = s;
			}
			count++;
		}
		
		System.out.println("##########################");
		System.out.println("Best Polynomial Set = " + best);
		System.out.println("Error = " + min);
	}

	private double[][] addColumns(int num, double[][] data) {
		double[][] adjustedData = new double[data.length][data[0].length + num];
		for(int i = 0; i < adjustedData.length; i++) {
			for(int j = 0; j < data[0].length; j++) {
				adjustedData[i][j] = data[i][j];
			}
		}
		return adjustedData;
	}
	private double[][] getSubset(int[] cols) {
		double[][] d = new double[allData.length][cols.length];

		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d[0].length; j++) {
				d[i][j] = allData[i][cols[j]];
			}
		}

		return d;
	}
}
