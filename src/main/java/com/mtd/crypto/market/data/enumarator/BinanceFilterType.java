package com.mtd.crypto.market.data.enumarator;

public enum BinanceFilterType {
    PRICE_FILTER,
    PERCENT_PRICE,
    LOT_SIZE,
    MIN_NOTIONAL,
    ICEBERG_PARTS,
    MARKET_LOT_SIZE,
    TRAILING_DELTA,
    MAX_NUM_ORDERS,
    MAX_NUM_ALGO_ORDERS,
    MAX_NUM_ICEBERG_ORDERS,
    MAX_POSITION,
    MAX_NUM_ORDERS_PER_PAIR,
    MAX_POSITION_PER_PAIR,
    MAX_NUM_ALGO_ORDERS_PER_PAIR,
    MIN_NOTIONAL_VALUE,
    NOTIONAL,
    PERCENT_PRICE_BY_SIDE,
    TRADING_PAIR,
    PERCENT_PRICE_BY_PAIR,
    PERCENT_PRICE_BY_BASE,
    PERCENT_PRICE_BY_QUOTE,
    MARKET_ORDER_QUANTITY,
    MARKET_BASE_ASSET,
    MAX_QUANTITY_PER_ORDER,
    MAX_QUANTITY_PER_BASE_ASSET,
    MAX_QUANTITY_PER_QUOTE_ASSET,
    MAX_ALGO_ORDERS_PER_PAIR,
    MAX_ALGO_QUANTITY_PER_ORDER,
    EXCHANGE_MAX_NUM_ORDERS,
    EXCHANGE_MAX_ALGO_ORDERS,
    EXCHANGE_MAX_NUM_ORDERS_PER_PAIR,
    EXCHANGE_MAX_ALGO_ORDERS_PER_PAIR,
    EXCHANGE_MAX_QUANTITY_PER_ORDER,
    EXCHANGE_MAX_QUANTITY_PER_PAIR,
    EXCHANGE_MAX_ALGO_QUANTITY_PER_ORDER,
    PERCENT_PRICE_BY_PAIR_AND_QUOTE,
    PERCENT_PRICE_BY_PAIR_AND_BASE,
    PERCENT_PRICE_BY_PAIR_AND_QUOTE_AND_BASE,
    EXCHANGE_MAX_QUANTITY_PER_PAIR_AND_QUOTE,
    EXCHANGE_MAX_QUANTITY_PER_PAIR_AND_BASE,
    EXCHANGE_MAX_QUANTITY_PER_PAIR_AND_QUOTE_AND_BASE,
    PERCENT_PRICE_BY_QUOTE_AND_BASE,
    PERCENT_PRICE_BY_QUOTE_AND_BASE_AND_PAIR,
    PERCENT_PRICE_BY_QUOTE_AND_BASE_AND_PAIR_AND_ICEBERG_PARTS,
    PERCENT_PRICE_BY_QUOTE_AND_BASE_AND_PAIR_AND_ICEBERG_PARTS_AND_MAX_NUM_ORDERS,
    UNKNOWN;


    public static String getValue(BinanceFilterType binanceFilterType){
        return binanceFilterType.toString();
    }
    // Method to convert a string value to BinanceFilterType enum
    public static BinanceFilterType fromString(String value) {
        for (BinanceFilterType type : BinanceFilterType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}
