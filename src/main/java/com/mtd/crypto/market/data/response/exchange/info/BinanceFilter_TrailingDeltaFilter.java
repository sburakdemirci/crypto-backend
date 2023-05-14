package com.mtd.crypto.market.data.response.exchange.info;

import lombok.Data;

@Data
public class BinanceFilter_TrailingDeltaFilter {
    private String filterType;
    private Integer minTrailingAboveDelta;
    private Integer maxTrailingAboveDelta;
    private Integer minTrailingBelowDelta;
    private Integer maxTrailingBelowDelta;
}
