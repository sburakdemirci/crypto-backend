package com.mtd.crypto.market.service;

import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderStatus;
import com.mtd.crypto.market.data.response.*;
import com.mtd.crypto.market.data.response.exchange.info.BinanceExchangeInfoResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)*/
class BinanceHttpClientTest {

/*    private static final String TEST_SYMBOL = "BTCUSDT";

    //TODO IMPORTANT!! Run this test daily with test environment yaml.


    @Autowired
    private BinanceHttpClient binanceHttpClient;

    @Autowired
    private BinanceService binanceService;


*//*
    @Test
    public void testServiceOco(){

        Double currentPrice = binanceHttpClient.getPrice(TEST_SYMBOL);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.9;
        Double limit = stop * 0.999;

        binanceService.placeOcoOrder(TEST_SYMBOL,100,sellPrice,stop);
    }*//*


    @Test
    public void placeOcoSellOrder() {
        Double currentPrice = binanceService.getCurrentPrice(TEST_SYMBOL);
        Double takeProfitPrice = currentPrice * 1.05;
        Double stopPrice = currentPrice * 0.98;
        BinanceOCOOrderResponse binanceOCOOrderResponse = binanceService.executeOcoSellOrder(TEST_SYMBOL, 0.0005, takeProfitPrice, stopPrice);
        System.out.println("");
    }


    @Order(1)
    @Test
    public void cancelAllOpenOcoOrders() {
        List<BinanceQueryOCOResponse> allOpenOrders = binanceHttpClient.getAllOpenOCOOrders();
        allOpenOrders.forEach(binanceOrderResponse -> {
            binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(binanceOrderResponse.getSymbol(), binanceOrderResponse.getOrderListId());
        });
    }

    @Order(2)
    @Test
    public void cancelAllOpenOrders() {
        List<BinanceOrderResponse> allOpenOrders = binanceHttpClient.getAllOpenOrders();
        allOpenOrders.forEach(binanceOrderResponse -> {
            binanceHttpClient.cancelOrderBySymbolAndOrderId(binanceOrderResponse.getSymbol(), binanceOrderResponse.getOrderId());
        });
    }


    @Test
    public void testGetSystemStatus() {
        BinanceSystemStatusResponse systemStatus = binanceHttpClient.getSystemStatus();
        assertNotNull(systemStatus);
        assertEquals(0, systemStatus.getStatus());
    }

    @Test
    public void testGetPrice() {
        Double price = binanceHttpClient.getPrice(TEST_SYMBOL);
        assertNotNull(price);
    }

    @Test
    public void testGetCandles() throws JSONException {
        int CANDLE_LIMIT = 5;
        List<BinanceCandleStickResponse> candles = binanceHttpClient.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, CANDLE_LIMIT);
        assertNotNull(candles);
        assertEquals(CANDLE_LIMIT, candles.size());
    }


    @Test
    public void testExecuteMarketBuyOrder() {
        BinanceOrderResponse orderResponse = binanceService.executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 100);
        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getOrderId());
        assertEquals(TEST_SYMBOL, orderResponse.getSymbol());
        assertEquals(BinanceOrderSide.BUY, orderResponse.getSide());
        // Add more assertions as needed
    }

*//*
    @Test
    public void testExecuteMarketSellOrder() {
        for (int i=0; i<50; i++){
            BinanceOrderResponse orderResponse = binanceHttpClient.executeMarketOrder(TEST_SYMBOL, BinanceOrderSide.SELL, 100);
            assertNotNull(orderResponse);
            assertNotNull(orderResponse.getOrderId());
            assertEquals(TEST_SYMBOL, orderResponse.getSymbol());
            assertEquals(BinanceOrderSide.SELL, orderResponse.getSide());
        }

        // Add more assertions as needed
    }*//*

*//*    @Test
    public void testExecuteLimitOrder() {
        Double price = binanceHttpClient.getPrice(TEST_SYMBOL);

        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceHttpClient.getExchangeInfoBySymbol(TEST_SYMBOL);

        BinanceDecimalInfoDto binanceDecimalInfoDto = BinanceDecimalInfoDto.builder()
                .priceTickSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize())
                .quantityStepSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getLotSizeFilter().getStepSize())
                .build();

        Double calculatedPrice = price * 0.9;

        Double calculatedQuantity = calculateQuantity(TEST_SYMBOL, 100);

        BinanceOrderResponse orderResponse = binanceHttpClient.executeLimitOrder(TEST_SYMBOL, BinanceOrderSide.BUY,
                new AdjustedDecimal(calculatedQuantity, binanceDecimalInfoDto.getQuantityStepSize()),
                new AdjustedDecimal(calculatedPrice,binanceDecimalInfoDto.getPriceTickSize()));


        assertNotNull(orderResponse);
        assertNotNull(orderResponse.getOrderId());
        assertEquals(TEST_SYMBOL, orderResponse.getSymbol());
        assertEquals(BinanceOrderSide.BUY, orderResponse.getSide());
        binanceHttpClient.cancelOrderBySymbolAndOrderId(orderResponse.getSymbol(), orderResponse.getOrderId());
        // Add more assertions as needed
    }*//*

*//*    @Test
    public void testGetAllOpenOrders() {
        List<BinanceOrderResponse> createdOrders = createMultipleLimitOrders(TEST_SYMBOL, BinanceOrderSide.BUY, 2, 100);
        List<BinanceOrderResponse> allOpenOrders = binanceHttpClient.getAllOpenOrders();
        assertNotNull(allOpenOrders);
        assertFalse(allOpenOrders.isEmpty());
        assertEquals(createdOrders.size(), allOpenOrders.size());
        allOpenOrders.forEach(order -> {
            assertNotNull(order.getOrderId());
            assertEquals(TEST_SYMBOL, order.getSymbol());
            assertEquals(BinanceOrderSide.BUY, order.getSide());
            // Add more assertions as needed
        });
        cancelMultipleOrders(createdOrders);
    }*//*

    @Test
    public void testGetAllOpenOrdersBySymbol() {
        List<BinanceOrderResponse> createdOrders = createMultipleLimitOrders(TEST_SYMBOL, BinanceOrderSide.BUY, 2, 100);
        List<BinanceOrderResponse> allOpenOrders = binanceHttpClient.getAllOpenOrdersBySymbol(TEST_SYMBOL);
        assertNotNull(allOpenOrders);
        assertFalse(allOpenOrders.isEmpty());
        assertEquals(createdOrders.size(), allOpenOrders.size());
        allOpenOrders.forEach(order -> {
            assertNotNull(order.getOrderId());
            assertEquals(TEST_SYMBOL, order.getSymbol());
            assertEquals(BinanceOrderSide.BUY, order.getSide());
            // Add more assertions as needed
        });
        cancelMultipleOrders(createdOrders);
    }

*//*    @Test
    public void testGetOrderById() {
        BinanceOrderResponse orderResponse = binanceHttpClient.executeMarketOrder(TEST_SYMBOL, BinanceOrderSide.BUY, 0.05);
        Long orderId = orderResponse.getOrderId();

        BinanceOrderResponse fetchedOrderResponse = binanceHttpClient.getOrderById(TEST_SYMBOL, orderId);
        assertNotNull(fetchedOrderResponse);
        assertEquals(orderId, fetchedOrderResponse.getOrderId());
        assertEquals(TEST_SYMBOL, fetchedOrderResponse.getSymbol());
        assertEquals(BinanceOrderSide.BUY, fetchedOrderResponse.getSide());
        // Add more assertions as needed
    }*//*

    @Test
    public void testCancelAllOpenOrdersBySymbol() {
        List<BinanceOrderResponse> createdOrders = createMultipleLimitOrders(TEST_SYMBOL, BinanceOrderSide.BUY, 2, 100);
        List<BinanceOrderResponse> canceledOrders = binanceHttpClient.cancelAllOrdersBySymbol(TEST_SYMBOL);
        assertNotNull(canceledOrders);
        assertFalse(canceledOrders.isEmpty());
        assertEquals(createdOrders.size(), canceledOrders.size());
        canceledOrders.forEach(order -> {
            assertNotNull(order.getOrderId());
            assertEquals(TEST_SYMBOL, order.getSymbol());
            assertEquals(BinanceOrderSide.BUY, order.getSide());
            assertEquals(BinanceOrderStatus.CANCELED, order.getStatus());
            // Add more assertions as needed
        });
    }

*//*    @Test
    public void testExecuteOCOSellOrder() {

        Double currentPrice = binanceHttpClient.getPrice(TEST_SYMBOL);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.999;
        Double limit = stop * 0.999;
        Integer quantityInDollars = 75;


        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceHttpClient.getExchangeInfoBySymbol(TEST_SYMBOL);

        Double adjustedSellPrice = adjust(sellPrice, exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize());
        Double adjustedStopPrice = adjust(stop, exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize());
        Double adjustedLimitPrice = adjust(limit, exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize());
        Double adjustedQuantity = adjust(calculateQuantity(TEST_SYMBOL, quantityInDollars), exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getLotSizeFilter().getStepSize());


        BinanceNewOCOOrderResponse ocoOrderResponse = binanceHttpClient.executeOCOSellOrder(TEST_SYMBOL, adjustedQuantity, adjustedSellPrice, adjustedStopPrice, adjustedLimitPrice);
        assertNotNull(ocoOrderResponse);


        BinanceCancelOCOResponse binanceCancelOCOResponse = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(ocoOrderResponse.getSymbol(), ocoOrderResponse.getOrderListId());

        assertNotNull(binanceCancelOCOResponse);
        assertEquals(binanceCancelOCOResponse.getOrderListId(), ocoOrderResponse.getOrderListId());

        // Add more assertions as needed
    }*//*

    private Double adjust(Double value, Double adjuster) {
        BigDecimal bdValue = BigDecimal.valueOf(value);
        BigDecimal bdTickPrice = BigDecimal.valueOf(adjuster);
        BigDecimal adjustedValue = bdValue.divide(bdTickPrice, 0, RoundingMode.DOWN).multiply(bdTickPrice).stripTrailingZeros();
        return adjustedValue.doubleValue();
    }


    private Double calculateQuantity(String symbol, Integer quantityInDollars) {
        Double price = binanceHttpClient.getPrice(symbol);
        return quantityInDollars / price;
    }


*//*    @Test
    public void testCancelOcoOrdersBySymbolAndOrderListId() {


        Double currentPrice = binanceHttpClient.getPrice(TEST_SYMBOL);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.9;
        Double limit = stop * 0.999;

        BinanceNewOCOOrderResponse ocoOrderResponse = binanceHttpClient.executeOCOSellOrder(TEST_SYMBOL, 0.05, sellPrice, stop, limit);

        assertNotNull(ocoOrderResponse);
        Long orderListId = ocoOrderResponse.getOrderListId();
        BinanceCancelOCOResponse cancelOCOResponse = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(TEST_SYMBOL, orderListId);
        assertNotNull(cancelOCOResponse);
        assertEquals(ocoOrderResponse.getOrderListId(), cancelOCOResponse.getOrderListId());
        // Add more assertions as needed
    }*//*

    @Test
    public void testGetAllOpenOCOOrders() {
        int ORDER_COUNT = 2;
        List<BinanceOCOOrderResponse> multipleOcoOrders = createMultipleOcoOrders(TEST_SYMBOL, ORDER_COUNT, 100);

        assertNotNull(multipleOcoOrders);
        assertFalse(multipleOcoOrders.isEmpty());

        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceHttpClient.getAllOpenOCOOrders();
        assertNotNull(allOpenOCOOrders);

        // Verify orderListId, side, symbol, and list size
        allOpenOCOOrders.forEach(ocoOrder -> {
            assertNotNull(ocoOrder.getOrderListId());
            assertEquals(TEST_SYMBOL, ocoOrder.getSymbol());
            // Add more assertions for side if needed
        });
        assertEquals(ORDER_COUNT, allOpenOCOOrders.size());
    }

*//*    @Test
    public void testGetAllOCOOrders() {
        Double currentPrice = binanceHttpClient.getPrice(TEST_SYMBOL);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.9;
        Double limit = stop * 0.999;

        BinanceNewOCOOrderResponse ocoOrderResponse = binanceHttpClient.executeOCOSellOrder(TEST_SYMBOL, 0.05, sellPrice, stop, limit);
        BinanceCancelOCOResponse cancelOCOResponse = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(TEST_SYMBOL, ocoOrderResponse.getOrderListId());

        List<BinanceQueryOCOResponse> allOCOOrders = binanceHttpClient.getAllOCOOrders();
        assertNotNull(allOCOOrders);
        assertFalse(allOCOOrders.isEmpty());
        // Add more assertions as needed
    }*//*

    @Test
    public void testCancelOcoOrdersOneByOne() {
        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceHttpClient.getAllOpenOCOOrders();
        allOpenOCOOrders.forEach(ocoOrder -> {
            BinanceOCOOrderResponse cancelOCOResponse = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(ocoOrder.getSymbol(), ocoOrder.getOrderListId());
            assertNotNull(cancelOCOResponse);
            assertEquals(ocoOrder.getOrderListId(), cancelOCOResponse.getOrderListId());
            // Add more assertions as needed
        });
    }


    @Test
    public void getAccountInfo() {

        AccountData accountInfo = binanceHttpClient.getAccountInfo();
        System.out.println("");
    }

    @Test
    public void getUserAsset() {

        List<UserAssetResponse> userAsset = binanceHttpClient.getUserAsset();
        System.out.println("");
    }

    @Test
    public void testGetExchangeInfo() {
        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceHttpClient.getExchangeInfoBySymbol("ABC");
        assertNotNull(exchangeInfoBySymbol);
    }

*//*
    @Test
    public void testGetExchangeInfoAll() {
        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceHttpClient.getExchangeInfoAllSymbols();
        assertNotNull(exchangeInfoBySymbol);
    }*//*


    // Helper method to create multiple market orders
    private List<BinanceOrderResponse> createMultipleLimitOrders(String symbol, BinanceOrderSide side, int count, int quantity) {
        return null;
 *//*       Double price = binanceHttpClient.getPrice(symbol);
        price *= 1.10;
        List<BinanceOrderResponse> createdOrders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BinanceOrderResponse orderResponse = binanceHttpClient.executeLimitOrder(symbol, side, quantity, price);
            createdOrders.add(orderResponse);
        }
        return createdOrders;*//*
    }


    // Helper method to create multiple oco sell orders
    private List<BinanceOCOOrderResponse> createMultipleOcoOrders(String symbol, int count, int quantity) {
*//*        Double currentPrice = binanceHttpClient.getPrice(symbol);
        Double sellPrice = currentPrice * 1.10;
        Double stop = currentPrice * 0.9;
        Double limit = stop * 0.999;
        List<BinanceNewOCOOrderResponse> createdOrders = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BinanceNewOCOOrderResponse binanceNewOCOOrderResponse = binanceHttpClient.executeOCOSellOrder(symbol, quantity, sellPrice, stop, limit);
            createdOrders.add(binanceNewOCOOrderResponse);
        }
        return createdOrders;*//*
        return null;
    }

    // Helper method to cancel multiple orders
    private void cancelMultipleOrders(List<BinanceOrderResponse> orders) {
        orders.forEach(order -> {
            BinanceOrderResponse canceledOrder = binanceHttpClient.cancelOrderBySymbolAndOrderId(order.getSymbol(), order.getOrderId());
            assertNotNull(canceledOrder);
            assertEquals(order.getOrderId(), canceledOrder.getOrderId());
            assertEquals(order.getSymbol(), canceledOrder.getSymbol());
            assertEquals(order.getSide(), canceledOrder.getSide());
            assertEquals(BinanceOrderStatus.CANCELED, canceledOrder.getStatus());
            // Add more assertions as needed
        });
    }*/

}



