package nayak.Data;

import java.io.File;
import java.io.IOException;

import nayak.Abstract.Classifier;
import nayak.Optimization.Crossvalidation;
import nayak.Utility.Print;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.Matrix;

/**
 * Computes learning curves using the JFreeChart library.
 * @author Ashwin K Nayak
 *
 */
public class LearningCurve {

	Classifier c;
	Crossvalidation cv;
	double[] trainingParams;

	public int maxExamplesToUse = 1000;
	public boolean displayCurve = false;
	public boolean writeCurve = true;

	public LearningCurve(Classifier c, double[] trainingParams) {
		this.c = c;
		this.cv = new Crossvalidation(c.getData().getArray(), c.getLabels().getColumnPackedCopy(), 1123);
		//		Print.print(cv.labels);
		this.trainingParams = trainingParams;
	}

	public void computeTrainingExampleLC() {
		cv.generateRandomSets(0.49, 0.49, 0.02);

		final XYSeries series1 = new XYSeries("Training Error");
		final XYSeries series2 = new XYSeries("Validation Error");

		int count = 1;
		while (count < maxExamplesToUse) {
			System.out.println("Training with " + (count) + " example(s)");
			c.init(new Matrix(cv.getTrainingSet(count)), new Matrix(cv.getTrainingLabels(count), cv.getTrainingLabels(count).length));
			c.train(trainingParams);
			double trainError = c.getError(c.getData(), c.getLabels());
			Matrix validationSet = new Matrix(cv.getValidationSet());
			Matrix validationLabels = new Matrix(cv.getValidationLabels(), cv.getValidationLabels().length);
			double validationError = c.getError(validationSet, validationLabels);
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

		if (writeCurve) {
			File file = new File("learningCurve_top2features.jpg");
			try {
				ChartUtilities.saveChartAsJPEG(file, chart, 1000, 500);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (displayCurve) {
			ChartFrame frame = new ChartFrame("First", chart);
			frame.pack();
			frame.setVisible(true);
		}
	}
}
