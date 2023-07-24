package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.market.data.binance.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.binance.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.configuration.SpotNormalTradingStrategyConfiguration;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalMarketOrderPositionCommandType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SpotNormalTraderCalculatorServiceTest {

    @Mock
    private BinanceService mockBinanceService;

    @Mock
    private SpotNormalTradingStrategyConfiguration mockSpotNormalTradingStrategyConfiguration;

    @InjectMocks
    private SpotNormalTraderCalculatorService calculatorService;


    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceHigherThanEntryPrice_ShouldReturnTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder().symbol("some-symbol").entry(100.0).takeProfit(110.0).stop(95.0).priceDropRequired(false).build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(101.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);
        boolean positionReadyToEnter = calculatorService.isPositionReadyToEnter(spotNormalTradeData);
        assertTrue(positionReadyToEnter);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceHigherThanEntryPrice_ShouldReturnTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder().symbol("some-symbol").entry(0.00045).takeProfit(110.0).stop(0.00043).priceDropRequired(false).build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00046);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);
        boolean positionReadyToEnter = calculatorService.isPositionReadyToEnter(spotNormalTradeData);
        assertTrue(positionReadyToEnter);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceLowerThanEntryPrice_HighLoss_ShouldReturnFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(95.0)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(99.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceLowerThanEntryPrice_HighLoss_ShouldReturnFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .takeProfit(0.00050)
                .stop(0.00042)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00044);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceEqualToEntryPrice_ShouldReturnTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(95.0)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(100.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);
        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceEqualToEntryPrice_ShouldReturnTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .takeProfit(0.00050)
                .stop(0.00043)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00045);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceSlightlyLessThanEntryPrice_HighLoss_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(96.0)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(99.99999);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceSlightlyHigherThanEntryPrice_ReturnsTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(96.0)
                .priceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(100.0001);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceGreaterThanSafeEntryPrice_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(95.0)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(111.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_Decimals_PriceDropRequired_CurrentPriceGreaterThanSafeEntryPrice_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .takeProfit(0.00070)
                .stop(0.00043)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.000496);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceEqualToSafeEntryPrice_ReturnsTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .stop(95.0)
                .takeProfit(140.0)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // =110
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(110.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_Decimal_PriceDropRequired_CurrentPriceEqualToSafeEntryPrice_StopLossHigh_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .takeProfit(0.00050)
                .stop(0.00043)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // =110
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.000495);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceLessThanSafeEntryPrice_HighLoss_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(96.0)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // = 110
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(109.0);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_Decimal__CurrentPriceLessThanSafeEntryPrice_ReturnsTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .takeProfit(0.00050)
                .stop(0.00043)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // = 110
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.00044);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceSlightlyGreaterThanSafeEntryPrice_HighLoss_ReturnsFalse() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .stop(95.0)
                .takeProfit(110.0)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(110.0000001);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceSlightlyLowerThanSafeEntryPrice_ReturnsTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(95.0)
                .priceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.001; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(99.999999);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_EnterCurrentPriceEntry_ReturnsTrue() {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .enterCurrentPrice(true)
                .takeProfit(110.0)
                .stop(95.0)
                .priceDropRequired(true)
                .build();

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceGreaterThanStopAndLessThanFirstProfit_ShouldReturnNone() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(102.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.NONE, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceGreaterThanFirstPartialProfit_ShouldReturnProfitSale1() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol")
                .gradualProfit(true)
                .build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(104.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_1, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceEqualsFirstPartialProfit_ShouldReturnProfitSale1() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .gradualProfit(true)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(103.3);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_1, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentPriceGreaterThanSecondPartialProfit_ShouldReturnProfitSale2() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol")
                .gradualProfit(true)
                .build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.singletonList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(107.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_2, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentPriceEqualsSecondPartialProfit_ShouldReturnProfitSale2() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol")
                .gradualProfit(true)
                .build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.singletonList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(106.6);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_2, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceGreaterThanTakeProfit_ShouldReturnExitProfit() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(111.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_PROFIT, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceEqualsTakeProfit_ShouldReturnExitProfit() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(110.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_PROFIT, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentPriceLessThenAverageEntryPrice_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.singletonList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(99.9);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_AFTER_PROFIT, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentPriceEqualsAverageEntryPrice_ShouldReturnNone() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.singletonList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(100.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPartialExitPercentageStep()).thenReturn(0.33);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.NONE, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceLessThenStopPrice_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(97.9);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_LOSS, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentPriceEqualsStopPrice_ShouldReturnNone() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(98.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);
        assertEquals(SpotNormalMarketOrderPositionCommandType.NONE, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentHypedUpAndLessThanTakeProfit_ShouldReturnPartialProfit1() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol")
                .gradualProfit(true)
                .build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(109.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.PROFIT_SALE_1, spotNormalMarketOrderPositionCommandType);
    }


    @Test
    void checkPartialOrFullExit_NoMarketSellOrders_GivenCurrentHypedDown_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.emptyList();
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(60.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_LOSS, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentHypedDownAfterFirstProfitSale_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Collections.singletonList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(60.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_AFTER_PROFIT, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_TwoMarketSellOrders_GivenCurrentHypedDownAfterSecondProfitSale_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Arrays.asList(new SpotNormalTradeMarketOrder(), new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(60.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_AFTER_PROFIT, spotNormalMarketOrderPositionCommandType);
    }

    @Test
    void checkPartialOrFullExit_OneMarketSellOrders_GivenCurrentPriceBetweenStopAndAverageEntryPrice_ShouldReturnExitStopLoss() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData
                .builder()
                .stop(98.0)
                .averageEntryPrice(100.0)
                .takeProfit(110.0)
                .symbol("some-symbol").
                build();

        List<SpotNormalTradeMarketOrder> marketOrders = Arrays.asList(new SpotNormalTradeMarketOrder());
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(99.0);

        SpotNormalMarketOrderPositionCommandType spotNormalMarketOrderPositionCommandType = calculatorService.checkPartialOrFullExit(spotNormalTradeData, marketOrders);

        assertEquals(SpotNormalMarketOrderPositionCommandType.EXIT_STOP_AFTER_PROFIT, spotNormalMarketOrderPositionCommandType);
    }


}