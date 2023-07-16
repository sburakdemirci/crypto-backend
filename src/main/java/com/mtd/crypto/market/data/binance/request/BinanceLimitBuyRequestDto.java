package com.mtd.crypto.market.data.binance.request;

import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderTimeInForce;
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
public class BinanceLimitBuyRequestDto extends BinanceRequestBase {

    private String symbol;
    private BinanceOrderSide side;
    private BinanceOrderType type;
    private BinanceOrderTimeInForce timeInForce;
    private AdjustedDecimal quantity;
    private AdjustedDecimal price;
}