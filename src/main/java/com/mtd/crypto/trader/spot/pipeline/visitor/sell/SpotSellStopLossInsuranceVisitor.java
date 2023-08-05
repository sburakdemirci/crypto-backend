package com.mtd.crypto.trader.spot.pipeline.visitor.sell;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.helper.SpotNormalTradeEstimatedProfitLossCalculator;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import org.springframework.stereotype.Component;

@Component
public class SpotSellStopLossInsuranceVisitor implements SpotVisitor {

    private static final Double INSURANCE_PERCENTAGE_TO_SELL = 0.20;

    //todo calculate this percentage by using estimated loss and estimated profit in the future


    /**
     * Checking if totalquantity equals to quantityLeftInPosition. That means there is no sale executed.
     *
     * @param context
     */
    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {

        if (context.getTradeData().getTotalQuantity().equals(context.getTradeData().getQuantityLeftInPosition())) {

            double currentEstimatedInsuranceProfit = SpotNormalTradeEstimatedProfitLossCalculator.calculateCurrentEstimatedProfitLoss(context.getTradeData(), context.getCurrentPrice()) * INSURANCE_PERCENTAGE_TO_SELL;

            if (currentEstimatedInsuranceProfit > SpotNormalTradeEstimatedProfitLossCalculator.calculatePossibleLoss(context.getTradeData())) {
                context.setDecision(new SpotOperationDecision(BinanceOrderSide.SELL, context.getTradeData().getQuantityLeftInPosition() * INSURANCE_PERCENTAGE_TO_SELL, SpotNormalTradeMarketOrderType.EXIT_ALL_STOP_LOSS));
            }
        }
    }


    @Override
    public String notificationText() {
        return "Stop loss insurance is executed for position";
    }
}
