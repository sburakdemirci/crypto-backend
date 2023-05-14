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
    private Long orderListId;
    private BinanceContingencyType contingencyType;
    private BinanceListStatusType listStatusType;
    private BinanceListOrderStatus listOrderStatus;
    private String listClientOrderId;
    private Long transactionTime;
    private String symbol;
    private List<BinanceNewOCOOrderResponse_Order> orders;
    private List<BinanceNewOCOOrderResponse_OrderReport> orderReports;

}


