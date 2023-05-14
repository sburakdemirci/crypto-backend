package com.mtd.crypto.market.service;

import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.response.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BinanceHttpClientTest {

    @Autowired
    private BinanceHttpClient binanceHttpClient;


    @Test
    public void getCurrentPrice() {
        Double etcValue = binanceHttpClient.getPrice("BTCUSDT");
        System.out.println("s");
    }

    @Test
    public void getSystemStatus(){
        BinanceSystemStatusResponse systemStatus = binanceHttpClient.getSystemStatus();
    }

    @Test
    public void getCandles() throws JSONException {
        List<BinanceCandleStickResponse> btc = binanceHttpClient.getCandles("BTCUSDT", BinanceCandleStickInterval.FOUR_HOURS, 5);
        System.out.println("s");
    }


    @Test
    public void placeMarketBuyOrder() throws Exception {
        BinanceOrderResponse btc = binanceHttpClient.executeMarketOrder("BTCUSDT", BinanceOrderSide.BUY, 100);

        System.out.println("s");
    }


    @Test
    public void placeLimitBuyOrder() throws Exception {
        BinanceOrderResponse btc = binanceHttpClient.executeLimitOrder("BTCUSDT", BinanceOrderSide.BUY, 100, 22000L);
        System.out.println("s");
    }


    @Test
    public void placeOCOSellOrder() throws Exception {
        BinanceNewOCOOrderResponse ocoSellResponse = binanceHttpClient.executeOCOSellOrder("BTCUSDT", 100, 30000.0, 24002.0, 24000.0);

        System.out.println("s");
    }


    @Test
    public void getAllOpenOrders() {
        List<BinanceOrderResponse> allOpenOrders = binanceHttpClient.getAllOpenOrders();
        System.out.println();
    }

    @Test
    public void getAllOpenOrdersBySymbol() {
        List<BinanceOrderResponse> allOrders = binanceHttpClient.getAllOpenOrders("BTCUSDT");
    }

/*    @Test
    public void getOrderById() throws Exception {
        BinanceNewOCOOrderResponse ocoSellResponse = binanceHttpClient.executeOCOSellOrder("BTCUSDT", 100, 30000.0, 24002.0, 24000.0);


        BinanceOrderResponse orderById = binanceHttpClient.getOrderById("BTCUSDT");
        System.out.println("");
    }*/


    @Test
    public void cancelAllOpenOrdersBySymbol() {

        List<BinanceOrderResponse> btcusdt = binanceHttpClient.cancelAllOrdersBySymbol("BTCUSDT");
        System.out.println("");
    }


    @Test
    public void getAllOCOOrders() {

        List<BinanceQueryOCOResponse> allOCOOrders = binanceHttpClient.getAllOCOOrders();
        System.out.println("");
    }


    @Test
    public void getAllOpenOCOOrders() {

        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceHttpClient.getAllOpenOCOOrders();
        System.out.println("");
    }

    @Test
    public void cancelOCOOrderBySymbolAndOrderListId() {

        BinanceCancelOCOResponse btcusdt = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId("BTCUSDT", 8468L);
        System.out.println("");
    }



    @Test
    public void cancelOcoOrdersOneByOne() {


        List<BinanceQueryOCOResponse> allOpenOCOOrders = binanceHttpClient.getAllOpenOCOOrders();

        allOpenOCOOrders.forEach(binanceQueryOCOResponse -> {
            BinanceCancelOCOResponse binanceCancelOCOResponse = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(binanceQueryOCOResponse.getSymbol(), binanceQueryOCOResponse.getOrderListId());
        });
        System.out.println("");
    }








}