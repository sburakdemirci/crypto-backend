package com.mtd.crypto.market.data.binance.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_PercentPriceBySideFilter {
    private String filterType;
    private Double bidMultiplierUp;
    private Double bidMultiplierDown;
    private Double askMultiplierUp;
    private Double askMultiplierDown;
    private Integer avgPriceMins;
}
