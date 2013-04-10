package hm_model;

import java.util.Random;

public abstract class Utils {
	public static long getRandomInRange(long start, long end) {
		Random rand = new Random();
		return rand.nextInt((int) (end - start + 1)) + start;
	}
}
