package com.mtd.crypto.trader.spot.helper;

import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;

public class SpotNormalTradePriceCalculatorHelper {

    public static double calculateGradualProfitPrice(SpotNormalTradeData spotNormalTradeData, Double partialExitPercentageStep) {
        return spotNormalTradeData.getAverageEntryPrice() + ((spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) * partialExitPercentageStep);
    }

}
