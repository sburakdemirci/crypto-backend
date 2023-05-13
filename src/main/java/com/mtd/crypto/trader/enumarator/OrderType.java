package com.mtd.crypto.trader.enumarator;

public enum OrderType {

    BUY_MARKET,
    BUY_LIMIT,
/*
    BUY_STOP_LIMIT, //IMPORTANT, THIS IS NOT SELL. THIS IS BUY. CHECK BINANCE APP
*/
    SELL_MARKET,
    SELL_LIMIT,
    SELL_STOP_LIMIT,
    SELL_OCO,
    SELL_TRAILING_STOP
}
