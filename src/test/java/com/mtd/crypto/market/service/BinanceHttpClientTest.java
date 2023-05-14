package com.mtd.crypto.market.service;

import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.data.response.BinanceOcoSellResponse;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
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
    public void getCandles() throws JSONException {
        List<BinanceCandleStickResponse> btc = binanceHttpClient.getCandles("BTCUSDT", BinanceCandleStickInterval.FOUR_HOURS, 5);
        System.out.println("s");
    }


    @Test
    public void placeMarketBuyOrder() throws Exception {
        BinanceOrderResponse btc = binanceHttpClient.executeMarketBuyOrder("BTCUSDT", 200);
        System.out.println("s");
    }


    @Test
    public void placeOCOSellOrder() throws Exception {
        BinanceOcoSellResponse ocoSellResponse = binanceHttpClient.executeOCOSellOrder("BTCUSDT", 100.0, 30000.0, 24002.0, 24000.0);

        System.out.println("s");
    }


    @Test
    public void getAllOpenOrders() {
        binanceHttpClient.getAllOpenOrders();
        System.out.println();
    }

    @Test
    public void getAllOpenOrdersBySymbol() {
        binanceHttpClient.getAllOpenOrdersBySymbol("BTCUSDT");
        System.out.println();
    }

    @Test
    public void getOrderById() {

        binanceHttpClient.getOrderById("BTCUSDT", 2468092L);
        System.out.println("");
    }


    @Test
    public void cancelAllOpenOrdersBySymbol() {

        binanceHttpClient.cancelAllOrdersBySymbol("BTCUSDT");
        System.out.println("");
    }


    @Test
    public void getAllOCOOrders() {

        binanceHttpClient.getAllOCOOrders();
        System.out.println("");
    }


    @Test
    public void getAllOpenOCOOrders() {

        BinanceOrderResponse allOpenOCOOrders = binanceHttpClient.getAllOpenOCOOrders();
        System.out.println("");
    }

    @Test
    public void cancelOCOOrderBySymbolAndOrderListId() {

        BinanceOrderResponse btcusdt = binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId("BTCUSDT", 8468L);
        System.out.println("");
    }


}