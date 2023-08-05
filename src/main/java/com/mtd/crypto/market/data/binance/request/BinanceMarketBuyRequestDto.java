package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.binance.enumarator.BinanceNewOrderResponseType;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderType;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
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