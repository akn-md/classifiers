package nayak.Data;

import java.io.File;
import java.io.IOException;

import nayak.Abstract.Classifier;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Computes learning curves using the JFreeChart library.
 * @author Ashwin K Nayak
 *
 */
public class LearningCurve {

	Classifier c;
	Crossvalidation cv;
	double[] trainingParams;

	public LearningCurve(Classifier c, Crossvalidation cv, double[] trainingParams) {
		this.c = c;
		this.cv = cv;
		this.trainingParams = trainingParams;
	}

	public void computeTrainingExampleLC() {
		cv.generateRandomSets(0.5, 0.5, 0.0);
		int numTrainingExamples = cv.getNumTrainingExamples();

		final XYSeries series1 = new XYSeries("Training Error");
		final XYSeries series2 = new XYSeries("Validation Error");

		int count = 1;
		while(count < 1000) {
			System.out.println("Training with " + (count) + " example(s)");
			double[][] data = cv.getTrainingSet(count);
			double[] labels = cv.getTrainingLabels(count);
			c.resetTraining(data, labels);
			c.train(trainingParams);
			double trainError = c.getError(Classifier.TRAINING);
			double validationError = c.getError(Classifier.VALIDATION);
			series1.add(count, trainError);
			series2.add(count, validationError);
			count++;
		}

		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);

		final JFreeChart chart = ChartFactory.createXYLineChart("Learning Curve", // chart title
				"# of Training Examples", // x axis label
				"Classification Error", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		File file = new File("learningCurve.jpg");
		try {
			ChartUtilities.saveChartAsJPEG(file, chart, 1000, 500);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ChartFrame frame = new ChartFrame("First", chart);
		frame.pack();
		frame.setVisible(true);

	}
}
