package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderType;
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