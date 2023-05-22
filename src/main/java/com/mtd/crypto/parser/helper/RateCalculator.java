package com.mtd.crypto.parser.helper;

public class RateCalculator {
    public static double calculateRate(double entry, double value) {
        return ((value - entry) / entry) * 100;

    }

    public static double doubleTwoDigits(double value) {
        return (double) Math.round(value * 100d) / 100d;
    }

}
