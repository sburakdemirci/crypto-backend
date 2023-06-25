package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.normal.configuration.SpotNormalTradingStrategyConfiguration;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.configurationprocessor.json.JSONException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
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
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceHigherThanEntryPrice_ShouldReturnTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder().symbol("some-symbol").entry(100.0).isPriceDropRequired(false).build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(101.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);
        boolean positionReadyToEnter = calculatorService.isPositionReadyToEnter(spotNormalTradeData);
        assertTrue(positionReadyToEnter);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceHigherThanEntryPrice_ShouldReturnTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder().symbol("some-symbol").entry(0.00045).isPriceDropRequired(false).build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00046);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);
        boolean positionReadyToEnter = calculatorService.isPositionReadyToEnter(spotNormalTradeData);
        assertTrue(positionReadyToEnter);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceLowerThanEntryPrice_ShouldReturnFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(99.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceLowerThanEntryPrice_ShouldReturnFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00044);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceEqualToEntryPrice_ShouldReturnTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(100.0);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_Decimals_GivenCurrentPriceEqualToEntryPrice_ShouldReturnTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(0.00045);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceSlightlyLessThanEntryPrice_ReturnsFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(99.99999);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_NonDrop_GivenCurrentPriceSlightlyHigherThanEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(false)
                .build();
        BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
        candle.setClose(100.0001);
        List<BinanceCandleStickResponse> candles = Collections.singletonList(candle);
        when(mockBinanceService.getCandles(anyString(), any(BinanceCandleStickInterval.class), anyInt())).thenReturn(candles);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceGreaterThanSafeEntryPrice_ReturnsFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(111.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }


    @Test
    void isPositionReadyToEnter_Decimals_PriceDropRequired_CurrentPriceGreaterThanSafeEntryPrice_ReturnsFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.000496);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceEqualToSafeEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // =110
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(110.0);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_Decimal_PriceDropRequired_CurrentPriceEqualToSafeEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // =110
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.000495);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceLessThanSafeEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // = 110
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(109.0);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_Decimal__CurrentPriceLessThanSafeEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(0.00045)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // = 110
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);
        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(0.00044);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceSlightlyGreaterThanSafeEntryPrice_ReturnsFalse() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.1; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(110.0000001);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertFalse(result);
    }

    @Test
    void isPositionReadyToEnter_PriceDropRequired_CurrentPriceSlightlyLowerThanSafeEntryPrice_ReturnsTrue() throws JSONException {
        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("some-symbol")
                .entry(100.0)
                .isPriceDropRequired(true)
                .build();
        double safeEntryPercentage = 0.001; // Adjust this value as needed

        when(mockBinanceService.getCurrentPrice(anyString())).thenReturn(99.999999);
        when(mockSpotNormalTradingStrategyConfiguration.getPriceDropSafeEntryPercentage()).thenReturn(safeEntryPercentage);

        boolean result = calculatorService.isPositionReadyToEnter(spotNormalTradeData);

        assertTrue(result);
    }


}