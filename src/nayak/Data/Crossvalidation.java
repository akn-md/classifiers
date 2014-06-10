package nayak.Data;

import java.io.Serializable;
import java.util.Random;

/**
 * Generates training, validation, and testing data sets.
 * Using the same random seed will create the same sets.
 * 
 * @author Ashwin
 *
 */
public class Crossvalidation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8608044984964574087L;

	public static void main(String[] args) {
		double[][] data = { { 1.0, 0.0, 1.0 }, { 1.0, 0.0, 2.0 }, { 1.0, 0.0, -1.0 }, { 1.0, 0.0, -2.0 } };
		double[] labels = { 0, 0, 1, 1 };
		Crossvalidation cv = new Crossvalidation(data, labels, 1);
		cv.generateRandomSet(0.6);
	}

	double[][] data;
	double[] labels;
	Random random;

	double[][] train, validation, test;
	double[] trainLabels, validationLabels, testLabels;

	public Crossvalidation(double[][] data, double[] labels, long seed) {
		this.data = data;
		this.labels = labels;
		random = new Random(seed);
	}

	public void generateRandomSet(double percentage) {
		int numTrainingExamples = (int) (data.length * percentage);
		int numValidationExamples = (int) ((data.length - numTrainingExamples) / 2.0);
		int numTestingExamples = data.length - numTrainingExamples - numValidationExamples;
		System.out.println("numTrainingExamples = " + numTrainingExamples);
		System.out.println("numValidationExamples = " + numValidationExamples);
		System.out.println("numTestingExamples = " + numTestingExamples);

		train = new double[numTrainingExamples][data[0].length];
		validation = new double[numValidationExamples][data[0].length];
		test = new double[numTestingExamples][data[0].length];

		trainLabels = new double[train.length];
		validationLabels = new double[validation.length];
		testLabels = new double[test.length];

		boolean[] used = new boolean[data.length];
		int trainingExampleIndex;
		for (int i = 0; i < numTrainingExamples; i++) {
			do {
				trainingExampleIndex = (int) (random.nextDouble() * data.length);
			} while (used[trainingExampleIndex] == true);
//			System.out.println(trainingExampleIndex);
			used[trainingExampleIndex] = true;
			train[i] = data[trainingExampleIndex];
			trainLabels[i] = labels[trainingExampleIndex];
		}

//		for (int i = 0; i < train.length; i++) {
//			System.out.println(train[i][0] + "," + train[i][1] + "," + train[i][2] + ", " + trainLabels[i]);
//		}

		for (int i = 0; i < numValidationExamples; i++) {
			do {
				trainingExampleIndex = (int) (random.nextDouble() * data.length);
			} while (used[trainingExampleIndex] == true);
//			System.out.println(trainingExampleIndex);
			used[trainingExampleIndex] = true;
			validation[i] = data[trainingExampleIndex];
			validationLabels[i] = labels[trainingExampleIndex];
		}
		
//		for (int i = 0; i < validation.length; i++) {
//			System.out.println(validation[i][0] + "," + validation[i][1] + "," + validation[i][2]);
//		}
		
		int count = 0;
		for(int i = 0; i < used.length; i++) {
			if(used[i] == false) {
				test[count] = data[i];
				testLabels[count] = labels[i];
				count++;
			}
		}
		
//		for (int i = 0; i < test.length; i++) {
//			System.out.println(test[i][0] + "," + test[i][1] + "," + test[i][2]);
//		}
	}
	
	public double[][] getTrainingSet() {
		return train;
	}
	
	public double[] getTrainingLabels() {
		return trainLabels;
	}
	
	public double[][] getValidationSet() {
		return validation;
	}
	
	public double[] getValidationLabels() {
		return validationLabels;
	}
	
	public double[][] getTestingSet() {
		return test;
	}
	
	public double[] getTestingLabels() {
		return testLabels;
	}

}
