package com.mtd.crypto.parser.data.response;

import com.mtd.crypto.parser.helper.RateCalculator;
import lombok.Data;

import java.util.List;


@Data
public class TradeDataResponse {
    private String coin;
    private Double entryPrice;
    private Double stopPrice;
    private Double currentPrice;
    private Double estimatedLossRate;
    private Double currentLossRate;
    private boolean reverse;
    private List<TradeDataProfit> profitList;


    public TradeDataResponse(String coin, Double entryPrice, Double stopPrice, Double currentPrice, boolean reverse, List<TradeDataProfit> profitList) {
        this.coin = coin;
        this.entryPrice = entryPrice;
        this.stopPrice = stopPrice;
        this.currentPrice = currentPrice;
        this.estimatedLossRate = RateCalculator.calculateRate(entryPrice, stopPrice);
        this.currentLossRate = RateCalculator.calculateRate(currentPrice, stopPrice);
        this.profitList = profitList;
        this.reverse = reverse;
    }

    public List<Object> convertToSheetData() {
   /*     List<Object> list = new ArrayList<>(Arrays.asList(coin, entryPrice, currentPrice, stopPrice, reverse, RateCalculator.DoubleTwoDigits(estimatedLossRate), RateCalculator.DoubleTwoDigits(currentLossRate)));
        profitList.forEach(tradeDataProfit -> {
            list.addAll(Arrays.asList(tradeDataProfit.getTakeProfitPrice(), RateCalculator.DoubleTwoDigits(tradeDataProfit.getTakeProfitRate()), RateCalculator.DoubleTwoDigits(tradeDataProfit.getCurrentTakeProfitRate())));
        });
        return list;*/
        return null;
    }
}

