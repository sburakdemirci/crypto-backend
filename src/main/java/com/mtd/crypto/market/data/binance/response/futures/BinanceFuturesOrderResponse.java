package com.mtd.crypto.market.data.binance.response.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesPositionSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderStatus;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderTimeInForce;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceFuturesOrderResponse {
    private String clientOrderId;
    private Double cumQty;
    private Double cumQuote;
    private Double executedQty;
    private Long orderId;
    private Double avgPrice;
    private Double origQty;
    private Double price;
    private Boolean reduceOnly;
    private BinanceOrderSide side;
    private BinanceFuturesPositionSide positionSide;
    private BinanceOrderStatus status;
    private Double stopPrice;
    private Boolean closePosition;
    private String symbol;
    private BinanceOrderTimeInForce timeInForce;
    private String type;
    private String origType;
    private Double activatePrice;
    private Double priceRate;
    private Long updateTime;
    private String workingType;
    private Boolean priceProtect;
}