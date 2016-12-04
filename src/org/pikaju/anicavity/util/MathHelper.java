package org.pikaju.anicavity.util;

import java.util.Random;

public class MathHelper {

	private static Random random = new Random();
	
	public static float sqrt(double v) {
		return (float) Math.sqrt(v);
	}

	public static float sin(float a) {
		return (float) Math.sin(Math.toRadians(a));
	}
	
	public static float cos(float a) {
		return (float) Math.cos(Math.toRadians(a));
	}
	
	public static float max(float a, float b) {
		return a > b ? a : b;
	}

	public static float min(float a, float b) {
		return a > b ? b : a;
	}

	public static Random getRandom() {
		return random;
	}
}