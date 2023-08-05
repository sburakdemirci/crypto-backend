package com.mtd.crypto.trader.spot.pipeline.visitor.sell;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotSellStopLossVisitor implements SpotVisitor {

    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        //TODO maybe add 4 hour candle close ? To prevent needle. But if price dropped to much handle this.
        if (context.getTradeData().getCurrentStop() < context.getCurrentPrice()) {
            context.setDecision(new SpotOperationDecision(BinanceOrderSide.SELL, context.getTradeData().getQuantityLeftInPosition(), SpotNormalTradeMarketOrderType.EXIT_ALL_STOP_LOSS));
        }
    }

    @Override
    public String notificationText() {
        return "Stop loss executed for position";
    }
}