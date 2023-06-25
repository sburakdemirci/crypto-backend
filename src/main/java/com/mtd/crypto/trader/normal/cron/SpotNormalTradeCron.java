package com.mtd.crypto.trader.normal.cron;

import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import com.mtd.crypto.trader.normal.service.SpotNormalTraderCalculatorService;
import com.mtd.crypto.trader.normal.service.SpotNormalTraderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SpotNormalTradeCron {


    private final SpotNormalTradeDataService dataService;
    private final SpotNormalTraderCalculatorService calculatorService;
    private final SpotNormalTraderService traderService;

    @Scheduled(cron = "* * * * *")
    public void checkAndEnterPositions() {
        List<SpotNormalTradeData> positionWaitingTrades = dataService.findTradesByStatus(TradeStatus.POSITION_WAITING);

        positionWaitingTrades.forEach(trade -> {
            try {
                if (calculatorService.isPositionReadyToEnter(trade)) {
                    traderService.enterPosition(trade);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //TELEGRAMS
            }
        });
    }

    @Scheduled(cron = "* * * * *")
    public void checkAndExitPositions() {
        //IMPORTANT, IF YOU NEED TO ADD SOMETHING LIKE TradeStatus.IN_PARTIAL_PROFIT_STATE, you need to fetch it as well to calculate in position Data.
        List<SpotNormalTradeData> tradesInPosition = dataService.findTradesByStatus(TradeStatus.IN_POSITION);

        tradesInPosition.forEach(trade -> {
            try {
                List<SpotNormalTradeMarketOrder> partialProfitOrders = dataService.findMarketOrderByParentTradeAndType(trade.getId(), SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
                SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(trade, partialProfitOrders);

                switch (spotNormalMarketOrderPositionCommandType) {
                    case EXIT_STOP_LOSS -> traderService.fullStopLossExit(trade);
                    case PROFIT_SALE_1 -> traderService.firstPartialProfit(trade);
                    case PROFIT_SALE_2 -> traderService.secondPartialProfit(trade);
                    case EXIT_PROFIT -> traderService.fullProfitExit(trade);
                    default ->
                            log.info("Position is not ready to exit. TradeId: {} TradePair: {} CommandType: {}", trade.getId(), trade.getSymbol(), spotNormalMarketOrderPositionCommandType);
                }

            } catch (Exception e) {
                e.printStackTrace();
                //telegramss
            }
        });
    }
}
