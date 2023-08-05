package com.mtd.crypto.trader.spot.pipeline.visitor.sell;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.helper.SpotNormalTradePriceCalculatorHelper;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotSellHalfGradualProfitVisitor implements SpotVisitor {

    private static final double HALF = 0.5;

    /**
     * Checking if quantity left in position is more than %50 of total quantity. To make sure this is not executed before and its makes sense to sell it
     *
     * @param context
     * @throws PipelineStopException
     */
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {

        if (context.getTradeData().getQuantityLeftInPosition() > context.getTradeData().getTotalQuantity() * HALF) {

            double gradualProfitPrice = SpotNormalTradePriceCalculatorHelper.calculateGradualProfitPrice(context.getTradeData(), HALF);
            if (context.getCurrentPrice() >= gradualProfitPrice) {
                context.setDecision(new SpotOperationDecision(BinanceOrderSide.SELL, context.getTradeData().getQuantityLeftInPosition() * HALF, SpotNormalTradeMarketOrderType.GRADUAL_PROFIT));
            }
        }
    }

    @Override
    public String notificationText() {
        return "Spot half gradual profit executed";
    }

    //todo burak insurance sell might be happen before this. So check for market orders. Or check quantityLEft in position and totalQuantity. Yes check if QuantityleftInPosition not lower than %50 percent of total quantity.

}
