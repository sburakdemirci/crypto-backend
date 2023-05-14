package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_MarketLotSizeFilter {
    private String filterType;
    private Double minQty;
    private Double maxQty;
    private Double stepSize;
}
