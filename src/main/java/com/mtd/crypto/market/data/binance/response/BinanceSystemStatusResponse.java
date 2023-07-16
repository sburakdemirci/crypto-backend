package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceSystemStatusResponse {
    private int status; // 0 - normal, 1 - system maintenance
    private String msg; // Additional information about the system status
}
