package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_MaxNumOrdersFilter {
    private String filterType;
    private Integer maxNumOrders;
}
