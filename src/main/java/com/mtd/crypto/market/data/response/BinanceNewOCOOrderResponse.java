package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BinanceNewOCOOrderResponse {
    private long orderListId;
    private BinanceContingencyType contingencyType;
    private BinanceListStatusType listStatusType;
    private BinanceListOrderStatus listOrderStatus;
    private String listClientOrderId;
    private long transactionTime;
    private String symbol;
    private List<BinanceNewOCOOrder_Order> orders;
    private List<BinanceNewOCOOrder_OrderReport> orderReports;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class BinanceNewOCOOrder_Order {
    private String symbol;
    private long orderId;
    private String clientOrderId;
}


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class BinanceNewOCOOrder_OrderReport {
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