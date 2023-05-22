package com.mtd.crypto.parser.helper;

import java.util.Arrays;
import java.util.List;

public class ExcelHeaderProvider {

    public static List<Object> getInitialDataHeaders() {
        return Arrays.asList("Coin", "Entry", "Current", "Stop", "Reverse", "Loss", "Current Loss", "TP1 Price", "TP1 Rate", "TP1 Current Rate", "TP2 Price", "TP2 Rate", "TP2 Current Rate");
    }
}
