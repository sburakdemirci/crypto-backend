package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.binance.BinanceContingencyType;
import com.mtd.crypto.market.data.binance.binance.BinanceListOrderStatus;
import com.mtd.crypto.market.data.binance.binance.BinanceListStatusType;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceQueryOCOResponse {
    private Long orderListId;
    private BinanceContingencyType contingencyType;
    private BinanceListStatusType listStatusType;
    private BinanceListOrderStatus listOrderStatus;
    private String listClientOrderId;
    private Long transactionTime;
    private String symbol;
    private List<BinanceQueryOCOResponse_Order> orders;
}


