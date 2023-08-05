package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderStatus;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderType;
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
    private Double commission;
    private BinanceOrderStatus status;
    private BinanceOrderTimeInForce timeInForce;
    private BinanceOrderType type;
    private BinanceOrderSide side;
    private List<BinanceOrderResponse_Fill> fills;
}


