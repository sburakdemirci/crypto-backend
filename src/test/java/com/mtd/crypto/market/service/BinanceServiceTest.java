package com.mtd.crypto.market.service;

import com.mtd.crypto.market.data.dto.BinanceDecimalInfoDto;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BinanceServiceTest {

    private static final String TEST_SYMBOL = "BTCUSDT";

    //TODO IMPORTANT!! Run this test daily with test environment yaml.


    @Autowired
    private BinanceService binanceService;

    @Order(1)
    @Test
    public void cancelAllOpenOcoOrders() {
        List<BinanceQueryOCOResponse> allOpenOrders = binanceService.getAllOpenOCOOrders();
        allOpenOrders.forEach(binanceOrderResponse -> {
            binanceService.cancelOcoOrderBySymbolAndOrderListId(binanceOrderResponse.getSymbol(), binanceOrderResponse.getOrderListId());
        });
    }

    @Order(2)
    @Test
    public void cancelAllOpenOrders() {
        List<BinanceOrderResponse> allOpenOrders = binanceService.getAllOpenOrders();
        allOpenOrders.forEach(binanceOrderResponse -> {
            binanceService.cancelOrderBySymbolAndOrderId(binanceOrderResponse.getSymbol(), binanceOrderResponse.getOrderId());
        });
    }

    @Test
    public void testGetSystemStatus() {
        BinanceSystemStatusResponse systemStatus = binanceService.getSystemStatus();
        assertNotNull(systemStatus);
        assertEquals(0, systemStatus.getStatus());
    }

    @Test
    public void testGetPrice() {
        Double price = binanceService.getCurrentPrice(TEST_SYMBOL);
        assertNotNull(price);
    }

    @Test
    public void testGetCandles() throws JSONException {
        int CANDLE_LIMIT = 5;
        List<BinanceCandleStickResponse> candles = binanceService.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, CANDLE_LIMIT);
        assertNotNull(candles);
        assertEquals(CANDLE_LIMIT, candles.size());
    }


    @Test
    public void testCancelOrderById() {
        int ORDER_COUNT = 1;
        BinanceOCOOrderResponse createdOrder = createMultipleOcoOrders(TEST_SYMBOL, ORDER_COUNT, 100).get(0);
        BinanceOCOOrderResponse cancelledOrder = binanceService.cancelOcoOrderBySymbolAndOrderListId(createdOrder.getSymbol(),createdOrder.getOrderListId());
        assertNotNull(cancelledOrder);
        assertEquals(createdOrder.getOrderListId(), cancelledOrder.getOrderListId());
        assertEquals(createdOrder.getSymbol(),cancelledOrder.getSymbol());
        assertEquals(createdOrder.getSymbol(),TEST_SYMBOL);
        assertEquals(cancelledOrder.getSymbol(),TEST_SYMBOL);
    }

    @Test
    public void testGetAllOpenOCOOrders() {
        int ORDER_COUNT = 2;
        List<BinanceOCOOrderResponse> multipleOcoOrders = createMultipleOcoOrders(TEST_SYMBOL, ORDER_COUNT, 100);
        assertNotNull(multipleOcoOrders);
        assertFalse(multipleOcoOrders.isEmpty());
        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceService.getAllOpenOCOOrders();
        assertNotNull(allOpenOCOOrders);
        allOpenOCOOrders.forEach(ocoOrder -> {
            assertNotNull(ocoOrder.getOrderListId());
            assertEquals(TEST_SYMBOL, ocoOrder.getSymbol());
            // Add more assertions for side if needed
        });
        assertEquals(ORDER_COUNT, allOpenOCOOrders.size());

        multipleOcoOrders.forEach(binanceOrderResponse -> {
            binanceService.cancelOcoOrderBySymbolAndOrderListId(binanceOrderResponse.getSymbol(), binanceOrderResponse.getOrderListId());
        });

    }

    @Test
    public void testCancelOcoOrdersOneByOne() {
        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceService.getAllOpenOCOOrders();
        allOpenOCOOrders.forEach(ocoOrder -> {
            BinanceOCOOrderResponse cancelOCOResponse = binanceService.cancelOcoOrderBySymbolAndOrderListId(ocoOrder.getSymbol(), ocoOrder.getOrderListId());
            assertNotNull(cancelOCOResponse);
            assertEquals(ocoOrder.getOrderListId(), cancelOCOResponse.getOrderListId());
            // Add more assertions as needed
        });
    }

    @Test
    public void testGetExchangeInfo() {
        BinanceDecimalInfoDto decimalInfo = binanceService.getDecimalInfo(TEST_SYMBOL);
        assertNotNull(decimalInfo);
    }

    // Helper method to create multiple limit orders
    private List<BinanceOrderResponse> createMultipleLimitOrders(String symbol, BinanceOrderSide side, int count, int quantity) {
        Double price = binanceService.getCurrentPrice(symbol);
        price *= 1.10;
        List<BinanceOrderResponse> createdOrders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BinanceOrderResponse orderResponse = binanceService.executeLimitOrder(symbol, side, quantity, price);
            createdOrders.add(orderResponse);
        }
        return createdOrders;
    }

    // Helper method to create multiple oco sell orders
    private List<BinanceOCOOrderResponse> createMultipleOcoOrders(String symbol, int count, int quantity) {
        Double currentPrice = binanceService.getCurrentPrice(symbol);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.9;
        List<BinanceOCOOrderResponse> createdOrders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BinanceOCOOrderResponse binanceNewOCOOrderResponse = binanceService.executeOcoSellOrder(symbol, quantity, sellPrice, stop);
            createdOrders.add(binanceNewOCOOrderResponse);
        }
        return createdOrders;
    }



}

