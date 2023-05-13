package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceMarketBuyRequestDto extends BinanceRequestBase {
    private String symbol;
    private BinanceOrderSide side;
    private BinanceOrderType type;
    private Integer quoteOrderQty;


}