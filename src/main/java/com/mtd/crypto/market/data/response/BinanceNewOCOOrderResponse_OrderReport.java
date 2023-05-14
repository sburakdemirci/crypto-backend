package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.*;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceNewOCOOrderResponse_OrderReport {
    private String symbol;
    private long orderId;
    private long orderListId;
    private String clientOrderId;
    private long transactTime;
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private BinanceOrderStatus status;
    private BinanceOrderTimeInForce timeInForce;
    private BinanceOrderType type;
    private BinanceOrderSide side;
    private String stopPrice;
    private long workingTime;
    private BinanceSelfTradePreventionMode selfTradePreventionMode;
}
