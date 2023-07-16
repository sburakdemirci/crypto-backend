package com.mtd.crypto.market.data.binance.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_LotSizeFilter {
    private String filterType;
    private Double minQty;
    private Double maxQty;
    private Double stepSize;
}
