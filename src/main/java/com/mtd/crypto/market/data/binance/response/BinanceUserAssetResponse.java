package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class BinanceUserAssetResponse {

    private String asset;
    private BigDecimal free;
    private BigDecimal locked;
    private BigDecimal freeze;
    private BigDecimal withdrawing;
    private BigDecimal ipoable;
    private BigDecimal btcValuation;


    public BinanceUserAssetResponse adjustPrecision(int scale) {
        this.free = free.setScale(scale, RoundingMode.HALF_DOWN);
        this.locked = locked.setScale(scale, RoundingMode.HALF_DOWN);
        this.freeze = freeze.setScale(scale, RoundingMode.HALF_DOWN);
        this.withdrawing = withdrawing.setScale(scale, RoundingMode.HALF_DOWN);
        this.ipoable = ipoable.setScale(scale, RoundingMode.HALF_DOWN);
        this.btcValuation = btcValuation.setScale(scale, RoundingMode.HALF_DOWN);
        return this;
    }
}
