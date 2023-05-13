package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class OrderReportsItem{
	private String symbol;
	private String cummulativeQuoteQty;
	private BinanceOrderSide side;
	private Long orderListId;
	private String executedQty;
	private Long orderId;
	private String origQty;
	private String clientOrderId;
	private Long workingTime;
	private BinanceOrderType type;
	private String price;
	private long transactTime;
	private String selfTradePreventionMode;
	private String timeInForce;
	private String status;
	private String stopPrice;
}