package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.BinanceContingencyType;
import com.mtd.crypto.market.data.enumarator.BinanceListOrderStatus;
import com.mtd.crypto.market.data.enumarator.BinanceListStatusType;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceQueryOCOResponse {
    private long orderListId;
    private BinanceContingencyType contingencyType;
    private BinanceListStatusType listStatusType;
    private BinanceListOrderStatus listOrderStatus;
    private String listClientOrderId;
    private long transactionTime;
    private String symbol;
    private List<BinanceQueryOCOResponse_Order> orders;
}


