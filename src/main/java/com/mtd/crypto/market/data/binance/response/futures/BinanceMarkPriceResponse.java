package com.mtd.crypto.market.data.binance.response.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceMarkPriceResponse {
    private String symbol;
    private Double markPrice;
    private Double indexPrice;
    private Double estimatedSettlePrice;
    private Double lastFundingRate;
    private Long nextFundingTime;
    private Double interestRate;
    private Long time;
}