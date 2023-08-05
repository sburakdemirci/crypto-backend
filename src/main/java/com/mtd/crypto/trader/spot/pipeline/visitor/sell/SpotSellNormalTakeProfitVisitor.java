package com.mtd.crypto.trader.spot.pipeline.visitor.sell;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotSellNormalTakeProfitVisitor implements SpotVisitor {
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        if (context.getCurrentPrice() >= context.getTradeData().getTakeProfit()) {
            context.setDecision(new SpotOperationDecision(BinanceOrderSide.SELL, context.getTradeData().getQuantityLeftInPosition(), SpotNormalTradeMarketOrderType.EXIT_ALL_PROFIT));
        }
    }


    @Override
    public String notificationText() {
        return "Take profit executed for position";
    }
}
