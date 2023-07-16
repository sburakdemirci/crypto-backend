package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceOcoRequestDto extends BinanceRequestBase {

    private String symbol;
    private AdjustedDecimal quantity;
    private AdjustedDecimal stopPrice;
    private AdjustedDecimal price;
    private AdjustedDecimal stopLimitPrice;
    private BinanceOrderSide side;
    private BinanceOrderTimeInForce stopLimitTimeInForce;

}

