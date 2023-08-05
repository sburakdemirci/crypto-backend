package com.mtd.crypto.trader.spot.pipeline.visitor.buy;

import com.mtd.crypto.market.data.binance.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpotNormalBuyEnter4HoursCandleCloseVisitor implements SpotVisitor {


    private final BinanceService binanceService;

    @Override
    public void visit(SpotTradeContext context) throws PipelineStopException {

        List<BinanceCandleStickResponse> candles = binanceService.getCandles(context.getTradeData().getSymbol(), BinanceCandleStickInterval.FOUR_HOURS, 1);
        Double lastFourHourCandleClose = candles.get(0).getClose();

        if (lastFourHourCandleClose > context.getTradeData().getEntry()) {
            Double totalQuantityToBuy = (double) context.getTradeData().getPositionAmountInDollar() / context.getCurrentPrice();
            context.setDecision(new SpotOperationDecision(BinanceOrderSide.BUY, totalQuantityToBuy, SpotNormalTradeMarketOrderType.ENTRY));
        }
    }

    @Override
    public String notificationText() {
        return "Position started with 4 hour candle close";
    }


}
