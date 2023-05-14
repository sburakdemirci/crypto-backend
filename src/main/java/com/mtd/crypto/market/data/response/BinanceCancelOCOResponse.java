package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceCancelOCOResponse {
    private Long orderListId;
    private String contingencyType;
    private String listStatusType;
    private String listOrderStatus;
    private String listClientOrderId;
    private Long transactionTime;
    private String symbol;
    private List<BinanceCancelOCO_Order> orders;
    private List<BinanceCancelOCO_OrderReport> orderReports;

}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class BinanceCancelOCO_Order {
    private String symbol;
    private Long orderId;
    private String clientOrderId;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class BinanceCancelOCO_OrderReport {
    private String symbol;
    private String origClientOrderId;
    private Long orderId;
    private Long orderListId;
    private String clientOrderId;
    private String price;
    private String origQty;
    private String executedQty;
    private String cummulativeQuoteQty;
    private String status;
    private String timeInForce;
    private String type;
    private String side;
    private String stopPrice;
    private String selfTradePreventionMode;
}