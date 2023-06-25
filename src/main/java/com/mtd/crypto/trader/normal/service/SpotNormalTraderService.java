package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.notification.service.TelegramService;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@LoggableClass
@RequiredArgsConstructor
public class SpotNormalTraderService {

    private final SpotNormalTradeDataService dataService;
    private final SpotNormalTraderCalculatorService calculatorService;
    private final BinanceService binanceService;
    private final TelegramService telegramService;


    //TODO important, if one coin fails log it and send a message to telegram. Do not throw any responses from execute methods for enter and exit


    public void enterPosition(SpotNormalTradeData parentTrade) {
        Double walletBalance = binanceService.getBalanceBySymbol(parentTrade.getBaseTradingSymbol());
        Double tradeAmountInDollars = walletBalance / parentTrade.getWalletPercentage();

        try {
            BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithDollar(parentTrade.getSymbol(), BinanceOrderSide.BUY, tradeAmountInDollars.intValue());
            SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.ENTRY);
            dataService.enterPosition(parentTrade.getId(), spotNormalTradeMarketOrder);
            //Telegram Send message
        } catch (Exception e) {
            //TELEGRAMSS
        }
    }


    public void firstPartialProfit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition() / 3;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.partialProfit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //save market order
        //todo update quantity left and all other necessary columns
    }

    public void secondPartialProfit(SpotNormalTradeData parentTrade) {

        Double quantityToSell = parentTrade.getQuantityLeftInPosition() / 2;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.partialProfit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //save market order
        //todo update quantity left and all other necessary columns
    }

    public void fullProfitExit(SpotNormalTradeData parentTrade) {

        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.fullProfitExit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //save market order
        //todo update quantity left and all other necessary columns
    }

    public void fullStopLossExit(SpotNormalTradeData parentTrade) {

        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.fullStopLossExit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //save market order
        //todo update quantity left and all other necessary columns
    }


}
