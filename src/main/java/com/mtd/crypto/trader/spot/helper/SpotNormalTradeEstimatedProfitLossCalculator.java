package com.mtd.crypto.trader.spot.helper;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;

import java.util.List;

public class SpotNormalTradeEstimatedProfitLossCalculator {

    public static double calculateGradualProfits(SpotNormalTradeData tradeData, List<SpotNormalTradeMarketOrder> marketOrderList) {
        return marketOrderList.stream().filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.SELL).map(marketOrder -> marketOrder.getQuantity() * (marketOrder.getAveragePrice() - tradeData.getAverageEntryPrice())).mapToDouble(d -> d).sum();
    }

    public static double calculateCurrentEstimatedProfitLoss(SpotNormalTradeData tradeData, double currentPrice) {
        return tradeData.getQuantityLeftInPosition() * (currentPrice - tradeData.getAverageEntryPrice());
    }

    public static double calculateFinishedPositionProfitLoss(List<SpotNormalTradeMarketOrder> marketOrders) {
        Double entryCost = marketOrders.stream().filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.BUY).map(marketOrder -> marketOrder.getQuantity() * marketOrder.getAveragePrice()).mapToDouble(d -> d).sum();
        Double totalExitSells = marketOrders.stream().filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.SELL).map(marketOrder -> marketOrder.getQuantity() * marketOrder.getAveragePrice()).mapToDouble(d -> d).sum();

        return totalExitSells - entryCost;
    }


    public static double calculatePossibleLoss(SpotNormalTradeData tradeData) {
        return tradeData.getQuantityLeftInPosition() * (tradeData.getAverageEntryPrice() - tradeData.getCurrentStop());
    }
}
