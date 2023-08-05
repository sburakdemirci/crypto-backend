package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpotBuyLossAndProfitCompareVisitor implements SpotVisitor {

    private final BinanceService binanceService;

    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        double estimatedLoss = context.getCurrentPrice() - context.getTradeData().getCurrentStop();
        double estimatedProfit = context.getTradeData().getTakeProfit() - context.getCurrentPrice();

        if (estimatedLoss >= estimatedProfit) {
            throw new PipelineStopException("Estimated loss higher than estimated profit! Position cannot be started.");
        }
    }

    @Override
    public String notificationText() {
        return "";
    }
}
