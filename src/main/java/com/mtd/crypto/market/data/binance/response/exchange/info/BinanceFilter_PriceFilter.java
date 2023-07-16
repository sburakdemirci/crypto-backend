package com.mtd.crypto.market.data.binance.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceFilter_PriceFilter {
    private String filterType;
    private Double minPrice;
    private Double maxPrice;
    private Double tickSize;
}
