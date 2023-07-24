package com.mtd.crypto.market.data.binance.response.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAdjustLeverageResponse {
    private String symbol;
    private String maxNotionalValue;
    private Integer leverage;
}