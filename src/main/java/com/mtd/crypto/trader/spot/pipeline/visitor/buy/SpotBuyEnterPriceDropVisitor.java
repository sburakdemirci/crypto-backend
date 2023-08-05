package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpotBuyEnterPriceDropVisitor implements SpotVisitor {

    private static final double SAFE_ENTRY_PERCENTAGE = 0.005;
    private final BinanceService binanceService;

    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {
        Double currentPrice = binanceService.getCurrentPrice(context.getTradeData().getSymbol());

        double safeEntryPrice = context.getTradeData().getEntry() * (1 + SAFE_ENTRY_PERCENTAGE);
        if (safeEntryPrice > currentPrice) {
            Double totalQuantityToBuy = (double) context.getTradeData().getPositionAmountInDollar() / currentPrice;
            context.setDecision(new SpotOperationDecision(BinanceOrderSide.BUY, totalQuantityToBuy, SpotNormalTradeMarketOrderType.ENTRY));
        }
    }


    @Override
    public String notificationText() {
        return "Position started with price drop strategy";
    }


}
