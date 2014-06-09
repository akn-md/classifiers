package nayak.Statistics;
public class Corr {

	public static double covariance(double[] x, double[] y) {
		double meanX = Stat.mean(x);
		double meanY = Stat.mean(y);

		double sum = 0.0;
		for (int i = 0; i < x.length; i++) {
			double dx = x[i] - meanX;
			double dy = y[i] - meanY;
			sum += (dx * dy);
		}
		return sum / x.length;
	}

	public static double pearsonCorr(double[] x, double[] y) {
		double cov = covariance(x, y);
		double sdX = Stat.sd(x);
		double sdY = Stat.sd(y);
		return cov / (sdX * sdY);
	}

	public static double correlation(double[] xValues, double[] yValues) {

		double correlation = Double.NaN;

		double N = xValues.length;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = xValues[0];
		double mean_y = yValues[0];

		for (int i = 2; i <= N; i++) {

			double sweep = (i - 1.0) / i;
			double delta_x = xValues[i - 1] - mean_x;
			double delta_y = yValues[i - 1] - mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}

		double pop_sd_x = Math.sqrt(sum_sq_x / N);
		double pop_sd_y = Math.sqrt(sum_sq_y / N);
		double cov_x_y = sum_coproduct / N;
		correlation = cov_x_y / (pop_sd_x * pop_sd_y);

		return correlation;
	}
	
	public static double[] leastSquares(double[] x, double[] y) {
		double[] coeff = new double[2];
		coeff[0] = covariance(x, y)/Stat.variance(x); // Beta or slope
		coeff[1] = Stat.mean(y) - coeff[0]*Stat.mean(x); // Alpha or intercept
		return coeff;
	}
	
	public static double[] residual(double[] x, double[] y, double alpha, double beta) {
		double[] residual = new double[x.length];
		for(int i = 0; i < x.length; i++) {
			residual[i] = (alpha + beta*x[i]) - y[i];
		}
		return residual;
	}
	
	// Fraction of variability explained by the model
	public static double coeffOfDeter(double[] y, double[] residual) {
		return 1 - (Stat.variance(residual)/Stat.variance(y));
	}
	
//	public static void main(String[] args) {
//		double[] x = { 0, 1, 1, 2, 2, 3, 3, 4, 4, 5 };
//		double[] y = { 4, 4, 6, 6, 7, 7, 8, 9, 10, 10 };
//		System.out.println(covariance(x, y));
//		System.out.println(pearsonCorr(x, y));
//		System.out.println(correlation(x, y));
//		System.out.println("y=" + leastSquares(x,x)[0] + "x" + "+" + leastSquares(x,x)[1]);
//		System.out.println("r^2=" + coeffOfDeter(y, residual(x, y, leastSquares(x,y)[1], leastSquares(x,y)[0])));
//		System.out.println("r^2=" + correlation(x,y)*correlation(x,y));
//
//	}
}
