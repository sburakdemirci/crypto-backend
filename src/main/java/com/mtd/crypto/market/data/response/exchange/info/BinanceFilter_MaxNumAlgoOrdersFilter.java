package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_MaxNumAlgoOrdersFilter {
    private String filterType;
    private Integer maxNumAlgoOrders;
}
