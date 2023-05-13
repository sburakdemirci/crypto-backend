package com.mtd.crypto.market.data.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceOcoSellResponse{
	private String contingencyType;
	private String symbol;
	private int orderListId;
	private String listOrderStatus;
	private String listClientOrderId;
	private List<OrdersItem> orders;
	private List<OrderReportsItem> orderReports;
	private long transactionTime;
	private String listStatusType;
}