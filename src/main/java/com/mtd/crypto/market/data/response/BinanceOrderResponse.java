package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderStatus;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceOrderResponse {
    private String symbol;
    private long orderId;
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
}
