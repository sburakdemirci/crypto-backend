package com.mtd.crypto.market.data.binance.enumarator;

public enum BinanceCandleStickInterval {
    ONE_MINUTE("1m", 60000),
    THREE_MINUTES("3m", 180000),
    FIVE_MINUTES("5m", 300000),
    FIFTEEN_MINUTES("15m", 900000),
    THIRTY_MINUTES("30m", 1800000),
    ONE_HOUR("1h", 3600000),
    TWO_HOURS("2h", 7200000),
    FOUR_HOURS("4h", 14400000),
    SIX_HOURS("6h", 21600000),
    EIGHT_HOURS("8h", 28800000),
    TWELVE_HOURS("12h", 43200000),
    ONE_DAY("1d", 86400000),
    THREE_DAYS("3d", 259200000),
    ONE_WEEK("1w", 604800000),
    ONE_MONTH("1M", 2592000000L);

    private final String intervalId;
    private final Long milliseconds;

    BinanceCandleStickInterval(String intervalId, long milliseconds) {
        this.intervalId = intervalId;
        this.milliseconds = milliseconds;
    }

    public String getIntervalId() {
        return intervalId;
    }

    public Long getMilliseconds() {
        return milliseconds;
    }
}

