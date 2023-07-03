package com.mtd.crypto.trader.normal.helper;

import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;

public class SpotNormalTradePriceCalculatorHelper {


    public static double calculateFirstPartialExitPrice(SpotNormalTradeData spotNormalTradeData, Double partialExitPercentageStep) {
        return spotNormalTradeData.getAverageEntryPrice() + ((spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) * partialExitPercentageStep);
    }

    public static double calculateSecondPartialExitPrice(SpotNormalTradeData spotNormalTradeData, Double partialExitPercentageStep) {
        return spotNormalTradeData.getAverageEntryPrice() + ((spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) * partialExitPercentageStep * 2);

    }


}
