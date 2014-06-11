package nayak.Statistics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Basic descriptive stat measures
 * 
 * @author Ashwin K Nayak
 *
 */
public class Stat {

	public static double mean(double[] data) {
		double sum = 0;
		for (double d : data) {
			sum += d;
		}
		return sum / data.length;
	}

	public static double variance(double[] data) {
		double mean = mean(data);
		double sum = 0;
		for (double d : data) {
			sum += Math.pow((d - mean), 2);
		}
		return sum / data.length;
	}

	public static double sd(double[] data) {
		return Math.sqrt(variance(data));
	}

	private static HashMap<Double, Integer> getHist(double[] data) {
		HashMap<Double, Integer> hist = new HashMap<Double, Integer>();
		for (double d : data) {
			if (hist.get(d) == null)
				hist.put(d, 1);
			else
				hist.put(d, (hist.get(d) + 1));
		}
		return hist;
	}

	private static HashMap<Double, Double> getPMF(double[] data) {
		HashMap<Double, Double> pmf = new HashMap<Double, Double>();
		HashMap<Double, Integer> hist = getHist(data);
		Iterator<Entry<Double, Integer>> it = hist.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Double, Integer> pairs = (Map.Entry<Double, Integer>) it.next();
			pmf.put(pairs.getKey(), ((double) pairs.getValue() / data.length));
		}
		return pmf;
	}
	
	public double getCDF(double d, double[] data) {
		double count = 0;
		for(double dd:data) {
			if(dd <= d)
				count ++;
		}
		
		return count / data.length;
	}
}
