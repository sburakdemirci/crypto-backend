package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_IcebergPartsFilter {
    private String filterType;
    private Integer limit;
}
