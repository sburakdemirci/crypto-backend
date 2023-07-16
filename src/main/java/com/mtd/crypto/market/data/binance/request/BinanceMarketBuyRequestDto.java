package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.binance.BinanceNewOrderResponseType;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderType;
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

    //todo burak calculate average price


}