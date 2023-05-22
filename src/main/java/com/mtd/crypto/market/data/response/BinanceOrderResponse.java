package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderStatus;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderType;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceOrderResponse {
    private String symbol;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private Long transactTime;
    private Double price;
    private Double origQty;
    private Double executedQty;
    private Double cummulativeQuoteQty;
    private BinanceOrderStatus status;
    private BinanceOrderTimeInForce timeInForce;
    private BinanceOrderType type;
    private BinanceOrderSide side;
    private List<BinanceOrderResponse_Fill> fills;
}


