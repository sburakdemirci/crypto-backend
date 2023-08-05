package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotBuyEnterWithoutCheckingPriceVisitor implements SpotVisitor {
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        Double totalQuantityToBuy = (double) context.getTradeData().getPositionAmountInDollar() / context.getCurrentPrice();
        context.setDecision(new SpotOperationDecision(BinanceOrderSide.BUY, totalQuantityToBuy, SpotNormalTradeMarketOrderType.ENTRY));
    }

    @Override
    public String notificationText() {
        return "Position started immediately with current price";
    }


}
