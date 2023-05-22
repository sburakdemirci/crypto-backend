package com.mtd.crypto.market.data.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.binance.BinanceExchangeInfoInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceRateLimitType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoResponse_RateLimit {
    private BinanceRateLimitType rateLimitType;
    private BinanceExchangeInfoInterval interval;
    private int intervalNum;
    private int limit;
}
