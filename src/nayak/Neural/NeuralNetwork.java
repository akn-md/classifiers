package nayak.Neural;

import java.util.Random;

import nayak.Abstract.Classifier;
import Jama.Matrix;

/**
 * Neural Network implementation
 * 
 * Features:
 * -Sigmoid activation function
 * -Backpropagation for training
 * -logistic cost function with regularization term
 * 
 * To add:
 * -gradient checking
 * -momentum
 * 
 * Training Steps:
 * 1) Randomly initialize weights - TESTED
 * 2) Implement forward propagation get activations for any example - TESTED
 * 3) Implement code to compute cost function - TESTED
 * 4) Implement backprop - TESTED
 * 
 * @author Ashwin K Nayak
 *
 */
public class NeuralNetwork extends Classifier {

	public static void main(String[] args) {
		int[] layers = { 2, 2, 1 };
		NeuralNetwork n = new NeuralNetwork(layers);
		double[][] in = { { 1.0, 1.0 }, { 1.0, 0.0 }, { 0.0, 0.0 }, { 0.0, 1.0 } };
		Matrix input = new Matrix(in);
		double[] l = { 1, 0, 1, 0 };
		Matrix labels = new Matrix(l, l.length);
		n.init(input, labels);
		n.getTrainingError();
		n.train(1000);
		
		double[] test =  {0.9, 0.1} ;
		n.print(n.predict(new Matrix(test, test.length)));
		//		n.getTrainingError();

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3792209131482070115L;

	protected Matrix[] weights; // weights to each neuron
	protected Matrix[] bigDelta; // for accumulating partial derivative terms

	protected int[] numNeurons;
	protected int totalLayers;
	protected int numInputs;
	protected int numOutputs;
	protected int numHiddenLayers;

	public double epsilon = 1.0; // for weight initialization
	public double regularizationCoefficient = 1.0;
	public double learningRate = 1.0;
	protected boolean regularizeWeights;

	public NeuralNetwork(int[] numNeurons) {
		this.numNeurons = numNeurons;
		totalLayers = numNeurons.length;
		numInputs = numNeurons[0];
		numOutputs = numNeurons[totalLayers - 1];
		numHiddenLayers = totalLayers - 2;

		initializeWeights();
	}

	private void initializeWeights() {
		long seed = System.currentTimeMillis();
		Random random = new Random(seed);

		weights = new Matrix[numNeurons.length - 1];

		for (int i = 0; i < weights.length; i++) {
			Matrix m = new Matrix(numNeurons[i + 1], numNeurons[i] + 1);

			for (int j = 0; j < m.getRowDimension(); j++) {
				for (int k = 0; k < m.getColumnDimension(); k++) {
					double val = random.nextDouble() * (2 * epsilon) - epsilon;
					m.set(j, k, val);
				}
			}

			weights[i] = m;
		}
	}

	@Override
	public void init(Matrix data, Matrix labels) {
		this.data = data;
		this.labels = labels;
	}

	@Override
	public void train(double[] params) {
		// TODO Auto-generated method stub
		train((int) params[0]);
	}

	public void train(int numIterations) {

		for (int i = 0; i < numIterations; i++) {
			for (int j = 0; j < data.getRowDimension(); j++)
				update(j);
			getTrainingError();
		}
	}

	/**
	 * Backpropagation (for online learning)
	 * @param i
	 */
	public void update(int i) {
		Matrix[] deltas = new Matrix[totalLayers - 1];

		// expected output
		Matrix label = new Matrix(labels.getArray()[i], labels.getArray()[i].length);
		double input[] = data.getArray()[i];
		Matrix[] activations = propagate(new Matrix(input, input.length));
		// actual output
		Matrix outputLayer = activations[activations.length - 1];

		// delta for output layer
		// delta = (expected - output)*output*(1 - output)
		Matrix delta = label.minus(outputLayer);
		Matrix ones = new Matrix(label.getRowDimension(), 1, 1.0);
		ones = ones.minus(outputLayer);
		ones = ones.arrayTimes(outputLayer);
		delta = delta.arrayTimes(ones);

		deltas[deltas.length - 1] = delta;
		
		// calculate weight change for output layer
		// weight += learningRate*(delta x prev_output)
		Matrix weightChange = delta.times(learningRate);
		weightChange = weightChange.times(activations[activations.length - 2].transpose());
		
		// adjust weights
		weights[weights.length - 1] = weights[weights.length - 1].plus(weightChange);

		// compute delta terms for hidden layers
		// delta = output*(1 - output)*(weights x prev_delta)
		for (int j = activations.length - 2; j > 0; j--) {
			// activations for current hidden layer
			Matrix layer = activations[j];

			// prev_delta
			Matrix prevDelta = deltas[j];

			// output*(1 - output)
			ones = new Matrix(layer.getRowDimension(), 1, 1.0);
			ones = ones.minus(layer);
			ones = ones.arrayTimes(layer);
			
			// weights that were just updated
			Matrix layerWeights = weights[j];
			
			// weights x prev_delta
			layerWeights = layerWeights.transpose().times(prevDelta);
			
			// calculate delta
			Matrix d = ones.arrayTimes(layerWeights);

			
			// remove bias unit error
			d = d.getMatrix(1, d.getRowDimension() - 1, 0, d.getColumnDimension() - 1);
			deltas[j - 1] = d;

			// calculate weight change
			Matrix wc = d.times(learningRate).times(activations[j -1].transpose());
			
			// adjust weights
			weights[j - 1] = weights[j - 1].plus(wc);
		}
	}

	/**
	 * Feedforward propagation
	 * @param input
	 * @return
	 */
	private Matrix[] propagate(Matrix input) {
		Matrix[] activations = new Matrix[totalLayers];

		// first layer of activations is just the input + bias unit
		Matrix m = new Matrix(input.getRowDimension() + 1, 1);
		m.set(0, 0, 1.0); // bias unit
		for (int i = 1; i < m.getRowDimension(); i++) {
			m.set(i, 0, input.get(i - 1, 0));
		}
		activations[0] = m;

		// compute activations for hidden layers and output layer
		for (int i = 1; i < activations.length; i++) {
			int numActivations = numNeurons[i];
			if (i != activations.length - 1) {
				numActivations++; // for bias unit
			}

			Matrix act = new Matrix(numActivations, 1);

			if (i != activations.length - 1)
				act.set(0, 0, 1); // bias unit

			Matrix layerWeights = weights[i - 1];
			Matrix z = layerWeights.times(activations[i - 1]); // z = theta*a

			// get sigmoid activations
			Matrix a = activationOf(z);

			if (i != activations.length - 1) {
				for (int j = 1; j < act.getRowDimension(); j++) {
					act.set(j, 0, a.get(j - 1, 0));
				}
				activations[i] = act;
			} else { // output layer
				activations[i] = a;
			}
		}

		return activations;
	}

	/**
	 * Returns sigmoid activations
	 * @param z
	 * @return
	 */
	private Matrix activationOf(Matrix z) {
		Matrix activations = new Matrix(z.getRowDimension(), z.getColumnDimension());

		for (int i = 0; i < activations.getRowDimension(); i++) {
			double val = z.get(i, 0);
			val = 1 / (1 + Math.exp(-1 * val));
			activations.set(i, 0, val);
		}

		return activations;
	}

	private Matrix predict(Matrix input) {
		Matrix[] activations = propagate(input);
		return activations[activations.length - 1];
	}

	/**
	 * Each row is an instance, each column is an output for one of the k output units
	 */
	@Override
	public Matrix getPredictions(Matrix data) {
		// TODO Auto-generated method stub
		Matrix predictions = new Matrix(data.getRowDimension(), numOutputs);

		double[][] d = data.getArray();

		for (int i = 0; i < predictions.getRowDimension(); i++) {
			Matrix input = new Matrix(d[i], d[i].length);
			Matrix output = predict(input);

			for (int j = 0; j < predictions.getColumnDimension(); j++) {
				predictions.set(i, j, output.get(j, 0));
			}
		}

//		print(predictions);
		return predictions;
	}

	/**
	 * Error = -1/m[sum of log cost function for all output neurons for all examples] + lambda/2m[sum of squared weight values]
	 */
	@Override
	public double getError(Matrix data, Matrix labels) {
		// TODO Auto-generated method stub
		Matrix predictions = getPredictions(data);

		double error = 0.0;

		for (int i = 0; i < predictions.getRowDimension(); i++) {
			for (int k = 0; k < predictions.getColumnDimension(); k++) {
				double actual = labels.get(i, k);
				double predicted = predictions.get(i, k);

				if(predicted  == 1.0)
					predicted = 0.999;
				if(predicted == 0.0)
					predicted = 0.0001;
				
				error += actual * Math.log(predicted) + (1 - actual) * Math.log(1 - predicted);

			}
		}

		error *= -1;
		error /= predictions.getRowDimension();
		System.out.println("Error = " + error);

//				double regTerm = 0.0;
//				// calculate regularization term, sum of square of weights for all layers and all neurons (except bias units)
//				for (int i = 0; i < weights.length; i++) { // go through each layer
//					Matrix m = weights[i];
//					for (int j = 0; j < m.getRowDimension(); j++) { // go through weights for each neuron
//						for (int k = 1; k < m.getColumnDimension(); k++) { // start at 1 to skip bias unit
//							regTerm += Math.pow(m.get(j, k), 2);
//						}
//					}
//				}
//		
//				regTerm *= regularizationCoefficient;
//				regTerm /= (2 * predictions.getRowDimension());
//		
//				error += regTerm;
//		
//				System.out.println("Error w/reg = " + error);

		return error;
	}

}
