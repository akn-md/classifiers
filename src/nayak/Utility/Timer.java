package nayak.Utility;

/**
 * Simple timer 
 * @author Ashwin K Nayak
 *
 */
public class Timer {

	static long start, stop;
	
	public static void start() {
		start = System.currentTimeMillis();
	}
	
	public static void stop() {
		stop = System.currentTimeMillis();
		System.out.println("Time = " + (stop - start) / 1000.0);
	}
}
