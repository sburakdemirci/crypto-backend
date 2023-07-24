package com.mtd.crypto.market.data.binance.response.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesPositionSide;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceFuturesPositionRiskResponse {
    private Double entryPrice;
    private String marginType;
    private Boolean isAutoAddMargin;
    private Double isolatedMargin;
    private Integer leverage;
    private Double liquidationPrice;
    private Double markPrice;
    private Integer maxNotionalValue;
    private Double positionAmt;
    private String notional;
    private String isolatedWallet;
    private String symbol;
    private Double unRealizedProfit;
    private BinanceFuturesPositionSide positionSide;
    private Long updateTime;
}