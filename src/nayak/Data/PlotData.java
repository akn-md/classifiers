package nayak.Data;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PlotData {

	public static void plot(double[] x, double[] y) {
		XYSeries series = new XYSeries("asdf");
		for (int i = 0; i < x.length; i++) {
			series.add(x[i], y[i]);
		}

		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);

		final JFreeChart chart = ChartFactory.createScatterPlot("Data", // chart title
				"Feature Value", // x axis label
				"Label", // y axis label
				data, // data
				PlotOrientation.VERTICAL, true, // include legend
				true, // tooltips
				false // urls
				);

		ChartFrame frame = new ChartFrame("First", chart);
		frame.pack();
		frame.setVisible(true);

	}
	
	public static void main(String[] args) {
		double[] x = {1, 2, 3, 4};
		double[] y = {0, 0, 1, 1};
		
		plot(x, y);
	}
}
