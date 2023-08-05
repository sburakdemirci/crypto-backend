package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.notification.TradeNotificationService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.exception.PipelineStopException;
import com.mtd.crypto.trader.spot.pipeline.data.SpotTradeContext;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;

import java.util.List;

public abstract class AbstractTrader {


    private final BinanceService binanceService;
    private final TradeNotificationService notificationService;
    private final SpotNormalTraderService traderService;

    protected AbstractTrader(BinanceService binanceService, TradeNotificationService notificationService, SpotNormalTraderService traderService) {
        this.binanceService = binanceService;
        this.notificationService = notificationService;
        this.traderService = traderService;
    }

    public abstract List<SpotVisitor> generateAlgorithm(SpotNormalTradeData tradeData);


    public void execute(SpotNormalTradeData tradeData) {

        SpotTradeContext context = new SpotTradeContext(tradeData, binanceService.getCurrentPrice(tradeData.getSymbol()));

        List<SpotVisitor> visitors = generateAlgorithm(tradeData);

        for (SpotVisitor visitor : visitors) {
            try {
                visitor.visit(context);
                if (context.getDecision() != null) {
                    traderService.executeDecision(tradeData, context.getDecision());

                    notificationService.sendInfoMessage("Symbol: " + context.getTradeData().getSymbol() + "\nTrade id: " + context.getTradeData().getId() + "\n" + visitor.notificationText());
                    return;
                }

            } catch (PipelineStopException e) {
                e.printStackTrace();
                notificationService.sendInfoMessage("Symbol: " + context.getTradeData().getSymbol() + "\nTrade id: " + context.getTradeData().getId() + "\n" + e.getMessage());
                return;
            } catch (Exception e) {
                e.printStackTrace();
                notificationService.sendErrorMessage("Symbol: " + context.getTradeData().getSymbol() + "\nTrade id: " + context.getTradeData().getId() + "\n" + e.getMessage());
                return;
            }
        }


    }
}
