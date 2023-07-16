package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceQueryOCOResponse_Order {
    private String symbol;
    private Long orderId;
    private String clientOrderId;
}
