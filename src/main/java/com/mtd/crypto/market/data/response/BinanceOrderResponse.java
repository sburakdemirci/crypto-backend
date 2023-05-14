package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderStatus;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
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
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private BinanceOrderStatus status;
    private BinanceOrderTimeInForce timeInForce;
    private BinanceOrderType type;
    private BinanceOrderSide side;
    private List<Fill> fills;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Fill {
        private String price;
        private String qty;
        private String commission;
        private String commissionAsset;
    }
}
