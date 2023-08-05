package com.mtd.crypto.trader.spot.enumarator;

public enum SpotNormalTradeMarketOrderType {
    ENTRY,
    GRADUAL_PROFIT,
    EXIT_ALL_PROFIT,
    EXIT_ALL_STOP_LOSS,
    EXIT_STOP_AFTER_PROFIT,
    POSITION_CANCELLED,
    MANUAL_CLOSE

    //add the conditions based on visitors. Each visitor will have a decision BUY or SELL. Market order type is basically indicates what kind of buy or sell decision was that.
}