package com.mtd.crypto.parser.data.response;

import com.mtd.crypto.parser.helper.RateCalculator;
import lombok.Data;


@Data
public class TradeDataProfit {
    private Double takeProfitPrice;
    private Double takeProfitRate;
    private Double currentTakeProfitRate;

    public TradeDataProfit(Double entryPrice, Double currentPrice, Double takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
        this.takeProfitRate = RateCalculator.calculateRate(entryPrice, takeProfitPrice);
        this.currentTakeProfitRate = RateCalculator.calculateRate(currentPrice, takeProfitPrice);
    }
}

