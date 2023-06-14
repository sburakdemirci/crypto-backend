package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.configuration.SpotNormalTradingStrategyConfiguration;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@LoggableClass
@RequiredArgsConstructor
public class SpotNormalTraderCalculatorService {

    private final SpotNormalTradingStrategyConfiguration spotNormalTradingStrategyConfiguration;
    private final BinanceService binanceService;

    public boolean isPositionReadyToEnter(SpotNormalTradeData spotNormalTradeData) throws JSONException {

        //TODO ONEMLI!!!!!   burayi iyi dusun. 4 saatlik mumda giris noktasi geldiyse ama mum cok buyukse, senin giris noktanda kari cok birakmis oluyorsun. Bunu iyi dusun
        //belki once son yarim saatlik mum yukardaysa falan giris yapabilirsin. Ek olarak 4 saatlige de bakabilirsin. Yada son yarim saatte hep giris noktasinin ustundeyse giris yapabilirsin
        if (spotNormalTradeData.isPriceDropRequired()) {

            Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeData.getSymbol());

            if (currentPrice <= spotNormalTradeData.getEntry() * spotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()) {
                return true;
            } else {
                return false;
            }
        } else {
            List<BinanceCandleStickResponse> candles = binanceService.getCandles(spotNormalTradeData.getSymbol(), BinanceCandleStickInterval.FOUR_HOURS, 1);
            Double lastFourHourCandleClose = candles.get(0).getClose();
            return lastFourHourCandleClose > spotNormalTradeData.getEntry();
        }
    }

    //Returns take partial profit or close position
    //todo if you take partial profit, change stoploss to average price

    public SpotNormalMarketOrderPositionCommandType isPositionProfitReady(SpotNormalTradeData spotNormalTradeData) {

        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeData.getSymbol());

        Double firstPartialExit = spotNormalTradeData.getAverageEntryPrice() + (spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) / spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep();
        Double secondPartialExit = spotNormalTradeData.getAverageEntryPrice() + (spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) / spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep() * 2;

        if (currentPrice > firstPartialExit) {
            List<SpotNormalTradeMarketOrder> marketOrders = spotNormalTradeData.getMarketOrders();
            List<SpotNormalTradeMarketOrder> profitSales = marketOrders.stream().filter(o -> o.getType() == SpotNormalTradeMarketOrderType.PROFIT_SALE).toList();

            if (profitSales.isEmpty()) {
                //first profit sale market sell %33 of quantity
                //update stop loss to entry price
                return SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_1;
            } else if (profitSales.size() == 1 && currentPrice >= secondPartialExit) {
                return SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_2;
                //second sale market %33 of quantity
                //maybe change the stopLoss to first profit Sale
            }

       /*     else if (currentPrice >= spotNormalTradeData.getTakeProfit() * SAFE_EXIT_PERCENTAGE) {
                //todo most probably you dont need this
                return SpotNormalMarketOrderPositionCommandType.EXIT;
                // eger take profit'e yuzde 1 kaldiysa cikabilirsin belki.  Yada stop noktasini ilk take profite cekersin ?
                //exit
                // return position still valid.
            }*/
        }

        return SpotNormalMarketOrderPositionCommandType.NONE;

    }
}
