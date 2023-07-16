package com.mtd.crypto.trader.normal.helper;

import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;

import java.util.List;

public class SpotNormalTradeEstimatedProfitLossCalculator {

    public static double calculateGradualProfits(SpotNormalTradeData tradeData, List<SpotNormalTradeMarketOrder> marketOrderList, double currentPrice) {
        return marketOrderList.stream().map(marketOrder -> marketOrder.getQuantity() * (tradeData.getAverageEntryPrice() - marketOrder.getAveragePrice())).mapToDouble(d -> d).sum();
    }

    public static double calculateCurrentEstimatedProfit(SpotNormalTradeData tradeData, List<SpotNormalTradeMarketOrder> marketOrderList, double currentPrice) {
        return tradeData.getQuantityLeftInPosition() * (currentPrice - tradeData.getAverageEntryPrice());
    }
}
