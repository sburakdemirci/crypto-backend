package com.mtd.crypto.parser.data.response;

import com.mtd.crypto.parser.helper.RateCalculator;
import lombok.Data;


@Data
public class TradeDataProfit {
    private double takeProfitPrice;
    private double takeProfitRate;
    private double currentTakeProfitRate;

    public TradeDataProfit(double entryPrice, double currentPrice,double takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
        this.takeProfitRate = RateCalculator.calculateRate(entryPrice, takeProfitPrice);
        this.currentTakeProfitRate = RateCalculator.calculateRate(currentPrice, takeProfitPrice);
    }
}

