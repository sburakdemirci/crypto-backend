package com.mtd.crypto.market.data.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.BinanceExchangeInfoInterval;
import com.mtd.crypto.market.data.enumarator.BinanceRateLimitType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoResponse_RateLimit {
    private BinanceRateLimitType rateLimitType;
    private BinanceExchangeInfoInterval interval;
    private int intervalNum;
    private int limit;
}
