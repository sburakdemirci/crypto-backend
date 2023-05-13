package com.mtd.crypto.market.service;

import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.request.BinanceMarketBuyRequest;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.data.response.BinanceOcoSellResponse;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest
class BinanceServiceTest {

    @Autowired
    private BinanceService binanceService;


    @Test
    public void getCurrentPrice() {
        Double etcValue = binanceService.getCurrentMarketPrice("BTCUSDT");
        System.out.println("s");
    }

    @Test
    public void getCandles() throws JSONException {
        List<BinanceCandleStickResponse> btc = binanceService.getCandles("BTCUSDT", BinanceCandleStickInterval.FOUR_HOURS, 5);
        System.out.println("s");
    }


    @Test
    public void placeMarketBuyOrder() throws Exception {
        BinanceOrderResponse btc = binanceService.placeMarketBuyOrder("BTCUSDT", 200);
        System.out.println("s");
    }


    @Test
    public void placeOCOSellOrder() throws Exception {
        BinanceOcoSellResponse ocoSellResponse = binanceService.placeOCOSellOrder("BTCUSDT", 100.0, 30000.0, 24000.0);
        System.out.println("s");
    }


    @Test
    public void getAllOpenOrders() {
        binanceService.getAllOpenOrders();
        System.out.println();
    }

    @Test
    public void getAllOpenOrdersBySymbol() {
        binanceService.getAllOpenOrdersBySymbol("BTCUSDT");
        System.out.println();
    }

    @Test
    public void getOrderById() {

        binanceService.getOrderById("BTCUSDT",2468092L);
        System.out.println("");
    }



    @Test
    public void cancelAllOpenOrdersBySymbol() {

        binanceService.cancelAllOrdersBySymbol("BTCUSDT");
        System.out.println("");
    }



    @Test
    public void getAllOCOOrders() {

        binanceService.getAllOCOOrders();
        System.out.println("");
    }



    @Test
    public void getAllOpenOCOOrders() {

        binanceService.getAllOpenOCOOrders();
        System.out.println("");
    }

    @Test
    public void cancelOCOOrderBySymbolAndOrderListId() {

        binanceService.cancelOcoOrdersBySymbolAndOrderListId("BTCUSDT",4857L);
        System.out.println("");
    }







}