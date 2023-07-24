package com.mtd.crypto.market.data.binance.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceExchangeInfoInterval;
import com.mtd.crypto.market.data.binance.enumarator.BinanceRateLimitType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoResponse_RateLimit {
    private BinanceRateLimitType rateLimitType;
    private BinanceExchangeInfoInterval interval;
    private int intervalNum;
    private int limit;
}
