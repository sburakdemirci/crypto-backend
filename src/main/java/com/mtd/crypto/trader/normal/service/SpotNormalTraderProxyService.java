package com.mtd.crypto.trader.normal.service;


import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotNormalTraderProxyService {

    private final SpotNormalTradeDataService dataService;
    private final SpotNormalTraderCalculatorService calculatorService;
    private final SpotNormalTraderService traderService;

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
                    case EXIT_STOP_AFTER_PROFIT -> traderService.fullStopLossExitAfterProfit(trade);
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
