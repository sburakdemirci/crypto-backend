package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_NotionalFilter {
    private String filterType;
    private Double minNotional;
    private Boolean applyMinToMarket;
    private Double maxNotional;
    private Boolean applyMaxToMarket;
    private Integer avgPriceMins;
}
