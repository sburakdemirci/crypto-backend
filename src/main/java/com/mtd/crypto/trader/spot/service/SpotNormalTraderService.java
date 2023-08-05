package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceOrderResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import com.mtd.crypto.trader.spot.pipeline.data.SpotOperationDecision;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
public class SpotNormalTraderService {

    private final SpotNormalTradeDataService dataService;
    private final BinanceService binanceService;

    //TODO important, if one coin fails log it and send a message to telegram. Do not throw any responses from execute methods for enter and exit


    //TODO burak be careful to set all necessary properties


    public void executeBuyDecision(SpotNormalTradeData tradeData, SpotOperationDecision decision) {
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(tradeData.getSymbol(), BinanceOrderSide.BUY, decision.getQuantity());
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(tradeData.getId(), binanceOrderResponse, decision.getType());
        dataService.enterPosition(tradeData.getId(), spotNormalTradeMarketOrder);
        //wallet percentage check in pipeline
    }

    public void executeSellDecision(SpotNormalTradeData tradeData, SpotOperationDecision decision) {
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(tradeData.getSymbol(), BinanceOrderSide.SELL, decision.getQuantity());
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(tradeData.getId(), binanceOrderResponse, decision.getType());

        if (tradeData.getQuantityLeftInPosition().equals(spotNormalTradeMarketOrder.getQuantity())) {
            dataService.finishPosition(tradeData.getId(), spotNormalTradeMarketOrder);
        } else {
            dataService.partialSell(tradeData.getId(), spotNormalTradeMarketOrder);
        }
    }


    public void executeDecision(SpotNormalTradeData tradeData, SpotOperationDecision decision) {
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(tradeData.getSymbol(), decision.getSide(), decision.getQuantity());
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(tradeData.getId(), binanceOrderResponse, decision.getType());

        if (decision.getSide() == BinanceOrderSide.BUY) {
            dataService.enterPosition(tradeData.getId(), spotNormalTradeMarketOrder);
        } else {
            if (tradeData.getQuantityLeftInPosition().equals(spotNormalTradeMarketOrder.getQuantity())) {
                dataService.finishPosition(tradeData.getId(), spotNormalTradeMarketOrder);
            } else {
                dataService.partialSell(tradeData.getId(), spotNormalTradeMarketOrder);
            }
        }
    }

    public SpotNormalTradeData manualClose(String tradeId) {
        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(tradeData.getSymbol(), BinanceOrderSide.SELL, tradeData.getQuantityLeftInPosition());
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(tradeData.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.MANUAL_CLOSE);
        return dataService.closeTradeManually(tradeData.getId(), spotNormalTradeMarketOrder);
    }


    //todo manual partial close
    //todo full manual close

/*    public SpotNormalTradeData cancelTradeExit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.POSITION_CANCELLED);
        return dataService.cancelTradeInPosition(parentTrade.getId(), spotNormalTradeMarketOrder);
    }*/


}
