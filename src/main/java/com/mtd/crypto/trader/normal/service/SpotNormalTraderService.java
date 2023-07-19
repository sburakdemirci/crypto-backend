package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceOrderResponse;
import com.mtd.crypto.market.data.binance.response.BinanceUserAssetResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
public class SpotNormalTraderService {

    private final SpotNormalTradeDataService dataService;
    private final BinanceService binanceService;


    //TODO important, if one coin fails log it and send a message to telegram. Do not throw any responses from execute methods for enter and exit


    public void enterPosition(SpotNormalTradeData parentTrade) {
        List<BinanceUserAssetResponse> wallet = binanceService.getWallet();
        BinanceUserAssetResponse btcusdt = wallet.stream().filter(w -> w.getAsset().equalsIgnoreCase("USDT")).findAny().orElseThrow(() -> new RuntimeException("BTCUSDT not found in wallet to start position"));
        Double tradeAmountInDollars = btcusdt.getFree().doubleValue() * parentTrade.getWalletPercentage();

        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithDollar(parentTrade.getSymbol(), BinanceOrderSide.BUY, tradeAmountInDollars.intValue());
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.ENTRY);
        dataService.enterPosition(parentTrade.getId(), spotNormalTradeMarketOrder);
        //Telegram Send message
    }


    public void firstPartialProfit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition() / 3;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.partialProfit(parentTrade.getId(), spotNormalTradeMarketOrder);
    }

    public void secondPartialProfit(SpotNormalTradeData parentTrade) {

        Double quantityToSell = parentTrade.getQuantityLeftInPosition() / 2;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.PARTIAL_PROFIT);
        dataService.partialProfit(parentTrade.getId(), spotNormalTradeMarketOrder);
    }

    public void fullProfitExit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.EXIT_ALL_PROFIT);
        dataService.fullProfitExit(parentTrade.getId(), spotNormalTradeMarketOrder);
    }

    public void fullStopLossExit(SpotNormalTradeData parentTrade) {

        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.EXIT_ALL_STOP_LOSS);
        dataService.fullStopLossExit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //todo update quantity left and all other necessary columns
    }


    public void fullStopLossExitAfterProfit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.EXIT_STOP_AFTER_PROFIT);
        dataService.fullStopLossExitAfterProfit(parentTrade.getId(), spotNormalTradeMarketOrder);
        //todo update quantity left and all other necessary columns
    }

    public SpotNormalTradeData cancelTradeExit(SpotNormalTradeData parentTrade) {
        Double quantityToSell = parentTrade.getQuantityLeftInPosition();
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrderWithQuantity(parentTrade.getSymbol(), BinanceOrderSide.SELL, quantityToSell);
        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = dataService.saveMarketOrder(parentTrade.getId(), binanceOrderResponse, SpotNormalTradeMarketOrderType.POSITION_CANCELLED);
        return dataService.cancelTradeInPosition(parentTrade.getId(), spotNormalTradeMarketOrder);
    }


}
