package com.mtd.crypto.market.data.binance.enumarator;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BinanceFuturesOrderType {
    LIMIT("LIMIT"),
    MARKET("MARKET"),
    STOP_TAKE_PROFIT("STOP/TAKE_PROFIT"),
    STOP_MARKET_TAKE_PROFIT_MARKET("STOP_MARKET/TAKE_PROFIT_MARKET"),
    TRAILING_STOP_MARKET("TRAILING_STOP_MARKET");


    @Getter
    private String value;

    @Override
    public String toString() {
        return value;
    }
}
