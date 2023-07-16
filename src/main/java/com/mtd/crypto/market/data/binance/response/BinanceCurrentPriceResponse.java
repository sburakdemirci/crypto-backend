package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceCurrentPriceResponse {
    public String symbol;
    public Double price;

}
