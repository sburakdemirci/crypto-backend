package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceQueryOCOResponse_Order {
    private String symbol;
    private long orderId;
    private String clientOrderId;
}
