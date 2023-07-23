package com.mtd.crypto.market.data.custom;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AdjustedDecimalTest {


    @Test
    public void toString_GivenPositiveValues_Adjuster1_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(10.5, 1.0);
        String result = decimal.toString();
        assertEquals("10", result);
    }

    @Test
    public void toString_GivenZeroValue_Adjuster1_ExpectedZero() {
        AdjustedDecimal decimal = new AdjustedDecimal(0.0, 1.0);
        String result = decimal.toString();
        assertEquals("0", result);
    }

    @Test
    public void toString_GivenPositiveValueAndPositiveAdjuster_Adjuster0_1_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(3.0, 0.1);
        String result = decimal.toString();
        assertEquals("3", result);
    }

    @Test
    public void toString_GivenPositiveValueAndPositiveAdjuster_Adjuster0_01_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(15.67, 0.01);
        String result = decimal.toString();
        assertEquals("15.67", result);
    }

    @Test
    public void toString_GivenPositiveValueAndSmallAdjuster_Adjuster0_000001_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(0.34442132131, 0.000001);
        String result = decimal.toString();
        assertEquals("0.344421", result);
    }

    @Test
    public void toString_GivenPositiveValueAndSmallAdjuster_Adjuster0_0000001_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(0.987654321, 0.0000001);
        String result = decimal.toString();
        assertEquals("0.9876543", result);
    }

    @Test
    public void toString_GivenValueLargerThanAdjuster_Adjuster0_1_ExpectedAdjustedValueWithDecimalAdjustment() {
        AdjustedDecimal decimal = new AdjustedDecimal(12.3456789, 0.1);
        String result = decimal.toString();
        assertEquals("12.3", result);
    }

    @Test
    public void toString_GivenValueSmallerThanAdjuster_Adjuster0_001_ExpectedZero() {
        AdjustedDecimal decimal = new AdjustedDecimal(0.000001, 0.001);
        String result = decimal.toString();
        assertEquals("0", result);
    }

    @Test
    public void toString_GivenValueEqualToAdjuster_Adjuster0_001_ExpectedValue() {
        AdjustedDecimal decimal = new AdjustedDecimal(0.001, 0.001);
        String result = decimal.toString();
        assertEquals("0.001", result);
    }



}