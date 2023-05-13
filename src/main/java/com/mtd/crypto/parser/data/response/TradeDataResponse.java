package com.mtd.crypto.parser.data.response;

import com.mtd.crypto.parser.helper.RateCalculator;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Data
public class TradeDataResponse {
    private String coin;
    private double entryPrice;
    private double stopPrice;
    private double currentPrice;
    private double estimatedLossRate;
    private double currentLossRate;
    private boolean reverse;
    private List<TradeDataProfit> profitList;


    public List<Object> convertToSheetData() {
        List<Object> list = new ArrayList<>(Arrays.asList(coin, entryPrice, currentPrice, stopPrice, reverse, RateCalculator.doubleTwoDigits(estimatedLossRate), RateCalculator.doubleTwoDigits(currentLossRate)));
        profitList.forEach(tradeDataProfit -> {
            list.addAll(Arrays.asList(tradeDataProfit.getTakeProfitPrice(), RateCalculator.doubleTwoDigits(tradeDataProfit.getTakeProfitRate()), RateCalculator.doubleTwoDigits(tradeDataProfit.getCurrentTakeProfitRate())));
        });
        return list;
    }

    public TradeDataResponse(String coin, double entryPrice, double stopPrice, double currentPrice, boolean reverse, List<TradeDataProfit> profitList) {
        this.coin = coin;
        this.entryPrice = entryPrice;
        this.stopPrice = stopPrice;
        this.currentPrice = currentPrice;
        this.estimatedLossRate = RateCalculator.calculateRate(entryPrice, stopPrice);
        this.currentLossRate = RateCalculator.calculateRate(currentPrice, stopPrice);
        this.profitList = profitList;
        this.reverse = reverse;
    }
}

