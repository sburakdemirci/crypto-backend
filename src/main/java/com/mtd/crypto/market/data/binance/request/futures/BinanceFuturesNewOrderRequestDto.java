package com.mtd.crypto.market.data.binance.request.futures;


import com.mtd.crypto.market.data.binance.enumarator.BinanceNewOrderResponseType;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.request.BinanceRequestBase;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BinanceFuturesNewOrderRequestDto extends BinanceRequestBase {

    private final BinanceNewOrderResponseType newOrderRespType = BinanceNewOrderResponseType.RESULT;
    private String symbol;
    private BinanceOrderSide side;
    //BinanceFuturesOrderType
    private String type;
    private AdjustedDecimal quantity;

}
