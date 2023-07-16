package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.binance.BinanceContingencyType;
import com.mtd.crypto.market.data.binance.binance.BinanceListOrderStatus;
import com.mtd.crypto.market.data.binance.binance.BinanceListStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BinanceOCOOrderResponse {
    private Long orderListId;
    private BinanceContingencyType contingencyType;
    private BinanceListStatusType listStatusType;
    private BinanceListOrderStatus listOrderStatus;
    private String listClientOrderId;
    private Long transactionTime;
    private String symbol;
    private List<BinanceOCOOrderResponse_Order> orders;
    private List<BinanceOCOOrderResponse_OrderReport> orderReports;

}


