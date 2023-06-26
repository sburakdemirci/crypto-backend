package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.configuration.SpotNormalTradingStrategyConfiguration;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalMarketOrderPositionCommandType;
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

    //TODO ONEMLI!!!!!   burayi iyi dusun. 4 saatlik mumda giris noktasi geldiyse ama mum cok buyukse, senin giris noktanda kari cok birakmis oluyorsun. Bunu iyi dusun
    //belki once son yarim saatlik mum yukardaysa falan giris yapabilirsin. Ek olarak 4 saatlige de bakabilirsin. Yada son yarim saatte hep giris noktasinin ustundeyse giris yapabilirsin

    public boolean isPositionReadyToEnter(SpotNormalTradeData spotNormalTradeData) throws JSONException {

        if (spotNormalTradeData.isPriceDropRequired()) {
            Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeData.getSymbol());
            Double safeEntryPrice = (spotNormalTradeData.getEntry() * (1 + spotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()));
            if (currentPrice <= safeEntryPrice) {
                return true;
            } else {
                return false;
            }
        } else {
            List<BinanceCandleStickResponse> candles = binanceService.getCandles(spotNormalTradeData.getSymbol(), BinanceCandleStickInterval.FOUR_HOURS, 1);
            Double lastFourHourCandleClose = candles.get(0).getClose();
            return lastFourHourCandleClose >= spotNormalTradeData.getEntry();
        }
    }

    //Returns take partial profit or close position
    //todo if you take partial profit, change stoploss to average price

    public SpotNormalMarketOrderPositionCommandType checkPartialOrFullExit(SpotNormalTradeData spotNormalTradeData, List<SpotNormalTradeMarketOrder> partialProfitOrders) {

        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeData.getSymbol());

        //Normal stop loss
        //TODO if price dropped too much, you might keep the coin, without sales
        if (partialProfitOrders.isEmpty() && currentPrice < spotNormalTradeData.getStop()) {
            return SpotNormalMarketOrderPositionCommandType.EXIT_STOP_LOSS;
        }

        //First partial profit executed before. This means stop limit is now the average entry price
        //TODO if price dropped too much, you might keep the coin. Without sales
        if (partialProfitOrders.size() > 0 && currentPrice < spotNormalTradeData.getAverageEntryPrice()) {
            return SpotNormalMarketOrderPositionCommandType.EXIT_STOP_LOSS;
        }

        double firstPartialExit = spotNormalTradeData.getAverageEntryPrice() + ((spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) * spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep());
        double secondPartialExit = spotNormalTradeData.getAverageEntryPrice() + ((spotNormalTradeData.getTakeProfit() - spotNormalTradeData.getAverageEntryPrice()) * spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()*2);

        //Exit coin regardless.
        if (currentPrice >= spotNormalTradeData.getTakeProfit()) {
            return SpotNormalMarketOrderPositionCommandType.EXIT_PROFIT;
        }

        //Second partial sale. If price reaches secondPartialExit without executing first partial exit, next cron will execute this one.
        //Maybe in the future I can support hype price increases. I can sell directly secondPartialExit.
        if (currentPrice >= secondPartialExit && partialProfitOrders.size() == 1) {
            return SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_2;
        }

        //First partial sale.
        if (currentPrice >= firstPartialExit && partialProfitOrders.isEmpty()) {
            return SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_1;
        }
        return SpotNormalMarketOrderPositionCommandType.NONE;
    }

}
