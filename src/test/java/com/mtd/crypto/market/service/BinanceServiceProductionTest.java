package com.mtd.crypto.market.service;

import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("prod")
public class BinanceServiceProductionTest {

    private static final String TEST_SYMBOL = "BTCUSDT";

    private static final Integer QUANTITY_IN_DOLLARS = 15;

    //TODO IMPORTANT!! Run this test daily with test environment yaml.


    @Autowired
    private BinanceService binanceService;

    @Test
    public void getSystemStatus() {
        BinanceSystemStatusResponse systemStatus = binanceService.getSystemStatus();
        assertNotNull(systemStatus);
        assertEquals(0, systemStatus.getStatus());
    }

    @Test
    public void getCurrentPrice() {
        Double price = binanceService.getCurrentPrice(TEST_SYMBOL);
        assertNotNull(price);
    }

    @Test
    public void getCandles() throws JSONException {
        int CANDLE_LIMIT = 5;
        List<BinanceCandleStickResponse> candles = binanceService.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, CANDLE_LIMIT);
        assertNotNull(candles);
        assertFalse(candles.isEmpty());
    }

    @Test
    public void placeMarketBuyOrder() {
        BinanceOrderResponse binanceOrderResponse = binanceService.executeMarketOrder(TEST_SYMBOL, BinanceOrderSide.BUY, QUANTITY_IN_DOLLARS);
    }

    @Test
    public void placeLimitBuyOrder() {
        Double currentPrice = binanceService.getCurrentPrice(TEST_SYMBOL);
        Double calculatedPrice = currentPrice * 0.95;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeLimitOrder(TEST_SYMBOL, BinanceOrderSide.BUY, QUANTITY_IN_DOLLARS, calculatedPrice);
    }

    @Test
    public void placeLimitSellOrder() {
        Double currentPrice = binanceService.getCurrentPrice(TEST_SYMBOL);
        Double calculatedPrice = currentPrice * 1.05;
        BinanceOrderResponse binanceOrderResponse = binanceService.executeLimitOrder(TEST_SYMBOL, BinanceOrderSide.SELL, QUANTITY_IN_DOLLARS, calculatedPrice);
    }

    @Test
    public void placeOcoSellOrder() {
        Double currentPrice = binanceService.getCurrentPrice(TEST_SYMBOL);
        Double takeProfitPrice = currentPrice * 1.05;
        Double stopPrice = currentPrice * 0.98;
        BinanceOCOOrderResponse binanceOCOOrderResponse = binanceService.executeOcoSellOrder(TEST_SYMBOL, QUANTITY_IN_DOLLARS, takeProfitPrice, stopPrice);
    }

    @Test
    public void cancelOcoOrder() {
        Long orderListId = 88186702L;
        BinanceOCOOrderResponse binanceOCOOrderResponse = binanceService.cancelOcoOrderBySymbolAndOrderListId(TEST_SYMBOL, orderListId);
    }

    @Test
    public void cancelLimitOrder() {
        Long orderId = 21218937161L;
        BinanceOrderResponse binanceOrderResponse = binanceService.cancelOrderBySymbolAndOrderId(TEST_SYMBOL, orderId);
    }


    @Test
    public void getUserData() {
        List<UserAssetResponse> userAsset = binanceService.getUserAsset();
    }


}

