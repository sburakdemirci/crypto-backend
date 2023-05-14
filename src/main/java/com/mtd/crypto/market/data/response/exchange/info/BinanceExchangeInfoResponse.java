package com.mtd.crypto.market.data.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoResponse {
    private String timezone;
    private Long serverTime;
    private List<BinanceExchangeInfoResponse_RateLimit> rateLimits;
    private List<BinanceExchangeInfoResponse_Symbol> symbols;
    private List<Object> exchangeFilters;
}


