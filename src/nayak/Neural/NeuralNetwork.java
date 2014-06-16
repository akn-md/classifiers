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
 * -logistic and squared error cost functions
 * -online learning
 * 
 * To add:
 * -fix bug in batch learning
 * -gradient checking
 * -momentum
 * -weight decay
 * -weight sharing
 * -model averaging
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

		double[] test = { 0.9, 0.1 };
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

	protected boolean useLogCostFunction = true; // should use a log cost function with sigmoid activation, use cross-entropy with softmax
	protected boolean useOnlineLearning = false;
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
		Random random = new Random(1123);

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

	private void initializeBigDelta() {
		bigDelta = new Matrix[weights.length];
		for (int i = 0; i < bigDelta.length; i++) {
			bigDelta[i] = new Matrix(weights[i].getRowDimension(), weights[i].getColumnDimension());
		}
	}

	private void updateWeights() {
		for (int i = 0; i < weights.length; i++) {
			Matrix delta = bigDelta[i].times(1 / data.getRowDimension());
			weights[i] = weights[i].plus(bigDelta[i]);
		}
	}

	public void train(int numIterations) {

		for (int i = 0; i < numIterations; i++) {
			initializeBigDelta();

			for (int j = 0; j < data.getRowDimension(); j++) {
				update(j);
			}

			if (!useOnlineLearning)
				updateWeights();

			getTrainingError();
		}
	}

	/**
	 * Backpropagation (for online learning)
	 * 
	 * Weights_new = weights_old - learningRate*(dE/dW)
	 * @param i
	 */
	public void update(int i) {
		// Overall calculation 
		// Weights_AB_new = weights_AB_old - learningRate*(dE/dW)
		// dE/dWab = deriv of Error w.r.t weights from a to b
		// dE/dWab = (dE/dIb)*(dIb/dWab) - via chain rule ---- dIb/dWab = Oa = output of neuron a
		// so dE/dWab = (dE/dIb)*Oa meaning the weight change of Wab depends 
		// on the sensitivity of the error to the input of neuron B and on the input signal Oa
		// Weights_AB_new = weights_AB_old - learningRate*(dE/dIb)*Oa
		// 2 cases for B:
		/// 1. B is an output neuron
		/// dE/dIb = 2E*f'(Ib)
		//// For sigmoid/logistic activation: f'(x) = f(x)*(1-f(x))
		//// For tanh activation: f'(x) = (1-f(x)^2)
		//// For linear function: f'(x) = 1
		/// Overall update for case 1 = weights_AB_old - learningRate*Oa*2E*f'(Ib)
		/// 2. B is a hidden neuron
		/// dE/dIb = (dE/dI0)*(dI0/dOb)*(dOb/dIb), 0 = output neuron
		//// dOb/dIb = f'(Ib)
		//// dI0/d0b = Wb0
		//// dE/dI0 = dE/dIb from previous layer
		//// so dE/dIb = (dE/dI0)*Wb0*f'(Ib)

		Matrix[] updates = new Matrix[weights.length];

		Matrix[] deltas = new Matrix[totalLayers - 1];

		// expected output
		Matrix label = new Matrix(labels.getArray()[i], labels.getArray()[i].length);
		double input[] = data.getArray()[i];
		Matrix[] activations = propagate(new Matrix(input, input.length));
		// actual output
		Matrix outputLayer = activations[activations.length - 1];

		// delta for output layer
		// delta = (expected - output)*output*(1 - output)
		Matrix delta = label.minus(outputLayer).times(2.0); // 2E
		Matrix ones = new Matrix(label.getRowDimension(), 1, 1.0);
		ones = ones.minus(outputLayer);
		ones = ones.arrayTimes(outputLayer); // f'(Ib)
		delta = delta.arrayTimes(ones); // 2E*f'(Ib)

		deltas[deltas.length - 1] = delta; // save delta for hidden layer

		// calculate weight change for output layer
		// weight += learningRate*(delta x prev_output)
		Matrix weightChange = delta.times(learningRate); // leraningRate*dE/dIb
		weightChange = weightChange.times(activations[activations.length - 2].transpose()); // gradient

		// save updates
		updates[weights.length - 1] = weightChange;
		bigDelta[weights.length - 1] = bigDelta[weights.length - 1].plus(weightChange);

		// compute delta terms for hidden layers
		// delta = output*(1 - output)*(weights x prev_delta)
		for (int j = activations.length - 2; j > 0; j--) {
			// activations for current hidden layer
			Matrix layer = activations[j]; // Ib

			// prev_delta
			Matrix prevDelta = deltas[j]; // dE/dI0

			// output*(1 - output)
			ones = new Matrix(layer.getRowDimension(), 1, 1.0);
			ones = ones.minus(layer);
			ones = ones.arrayTimes(layer); // f'(Ib)

			// weights 
			Matrix layerWeights = weights[j]; // Wb0

			// weights x prev_delta
			layerWeights = layerWeights.transpose().times(prevDelta); // Wb0*(dE/dI0)

			// calculate delta
			Matrix d = ones.arrayTimes(layerWeights); // dE/dIb

			// remove bias unit error
			d = d.getMatrix(1, d.getRowDimension() - 1, 0, d.getColumnDimension() - 1);
			deltas[j - 1] = d;

			// calculate weight change
			Matrix wc = d.times(learningRate).times(activations[j - 1].transpose()); // learningRate*dE/dW

			// save updates
			updates[j - 1] = wc;
			bigDelta[j - 1] = bigDelta[j - 1].plus(wc);
		}

		// update weights for online learning
		if (useOnlineLearning) {
			for (int w = 0; w < weights.length; w++) {
				weights[w] = weights[w].plus(updates[w]);
			}
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

				if (useLogCostFunction) {
					if (predicted == 1.0)
						predicted = 0.999;
					if (predicted == 0.0)
						predicted = 0.0001;

					error += actual * Math.log(predicted) + (1 - actual) * Math.log(1 - predicted);
				} else {
					error += Math.pow((actual - predicted), 2);

				}

			}
		}

		if (useLogCostFunction)
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
