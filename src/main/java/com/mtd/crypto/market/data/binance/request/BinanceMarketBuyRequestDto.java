package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.enumarator.BinanceNewOrderResponseType;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceMarketBuyRequestDto extends BinanceRequestBase {

    private final BinanceNewOrderResponseType newOrderRespType = BinanceNewOrderResponseType.FULL;
    private String symbol;
    private BinanceOrderSide side;
    private BinanceOrderType type;
    private AdjustedDecimal quantity;

}