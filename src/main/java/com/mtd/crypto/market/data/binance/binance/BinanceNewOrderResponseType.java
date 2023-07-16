package com.mtd.crypto.market.data.binance.binance;

public enum BinanceNewOrderResponseType {
    ACK("ACK"),
    RESULT("RESULT"),
    FULL("FULL");

    private final String value;

    BinanceNewOrderResponseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

