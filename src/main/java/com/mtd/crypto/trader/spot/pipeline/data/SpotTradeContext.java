package com.mtd.crypto.trader.spot.pipeline.data;

import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;


public class SpotTradeContext {
    private SpotNormalTradeData tradeData;
    private Double currentPrice;
    private SpotOperationDecision decision;

    public SpotTradeContext(SpotNormalTradeData tradeData, Double currentPrice) {
        this.tradeData = tradeData;
        this.currentPrice = currentPrice;
    }

    public SpotNormalTradeData getTradeData() {
        return tradeData;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public SpotOperationDecision getDecision() {
        return decision;
    }

    public void setDecision(SpotOperationDecision decision) {
        this.decision = decision;
    }
}

