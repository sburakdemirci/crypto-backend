package com.mtd.crypto.market.data.binance.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_MaxNumOrdersFilter {
    private String filterType;
    private Integer maxNumOrders;
}
