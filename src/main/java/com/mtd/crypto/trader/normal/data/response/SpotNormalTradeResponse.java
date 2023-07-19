package com.mtd.crypto.trader.normal.data.response;

import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotNormalTradeResponse {
    private SpotNormalTradeData tradeData;
    private List<SpotNormalTradeMarketOrder> marketOrders;
    private double currentPrice;
    private Double collectedProfit;
    private Double currentEstimatedProfitLoss;
    private Double finishedProfitLoss;
}
