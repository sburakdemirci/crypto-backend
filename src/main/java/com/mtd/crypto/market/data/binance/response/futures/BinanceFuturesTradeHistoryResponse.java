package com.mtd.crypto.market.data.binance.response.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesPositionSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceFuturesTradeHistoryResponse {
    private Boolean buyer;
    private Double commission;
    private String commissionAsset;
    private Long id;
    private Boolean maker;
    private Long orderId;
    private Double price;
    private Double qty;
    private Double quoteQty;
    private Double realizedPnl;
    private BinanceOrderSide side;
    private BinanceFuturesPositionSide positionSide;
    private String symbol;
    private Long time;
}

