package com.mtd.crypto.trader.spot.pipeline.data;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class SpotOperationDecision {
    BinanceOrderSide side;
    Double quantity;
    SpotNormalTradeMarketOrderType type;
}