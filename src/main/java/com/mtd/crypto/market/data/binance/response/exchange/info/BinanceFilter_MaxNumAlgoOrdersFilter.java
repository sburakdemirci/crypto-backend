package com.mtd.crypto.market.data.binance.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_MaxNumAlgoOrdersFilter {
    private String filterType;
    private Integer maxNumAlgoOrders;
}
