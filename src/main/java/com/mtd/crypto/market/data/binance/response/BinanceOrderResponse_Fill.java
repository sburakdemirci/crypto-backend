package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceOrderResponse_Fill {
    private Double price;
    private Double qty;
    private Double commission;
    private String commissionAsset;
    private Long tradeId;
}
