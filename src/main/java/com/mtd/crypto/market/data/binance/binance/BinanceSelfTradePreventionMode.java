package com.mtd.crypto.market.data.binance.binance;

public enum BinanceSelfTradePreventionMode {
    NONE,
    POSITIVE,
    NEGATIVE,
    REDUCE;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}