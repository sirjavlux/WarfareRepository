package com.coding.sirjavlux.utils;

public class MathUtil {
	public static double round(double value, int decimals) {
	    if (decimals < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, decimals);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
