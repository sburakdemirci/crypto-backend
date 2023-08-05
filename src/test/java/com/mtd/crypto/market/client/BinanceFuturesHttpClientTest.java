package com.mtd.crypto.market.client;

import com.mtd.crypto.market.data.binance.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.data.binance.response.exchange.info.BinanceExchangeInfoResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceFuturesOrderResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceFuturesTradeHistoryResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceMarkPriceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;


@SpringBootTest
@ActiveProfiles("dev")
class BinanceFuturesHttpClientTest {

    @Autowired
    BinanceFuturesHttpClient httpClient;

    @Test
    void getExchangeInfoBySymbol() {
        BinanceExchangeInfoResponse btcusdt = httpClient.getExchangeInfoBySymbol("BTCUSDT");

        System.out.println("");

    }

    @Test
    void getMarkPrice() {
        Double btcusdt = httpClient.getMarkPrice("BTCUSDT");
        System.out.println("");
    }

    @Test
    void getMarkPrices() {
        List<BinanceMarkPriceResponse> markPrices = httpClient.getMarkPrices();
        System.out.println("");
    }

    @Test
    void getLastPrice() {
        Double btcusdt = httpClient.getLastPrice("BTCUSDT");
        System.out.println("");
    }

    @Test
    void getLastPrices() {
        List<BinanceCurrentPriceResponse> lastPrices = httpClient.getLastPrices();
        System.out.println("");
    }

    @Test
    void executeMarketOrder() {
        String symbol = "BTCUSDT";
/*
        BinanceAdjustLeverageResponse binanceAdjustLeverageResponse = httpClient.adjustLeverage(symbol, 3);
*/
        /* httpClient.setMarginTypeIsolated(symbol);*/

        BinanceFuturesOrderResponse btcusdt = httpClient.executeMarketOrder(symbol, BinanceOrderSide.BUY, new AdjustedDecimal(0.005, 0.001));

        BinanceFuturesTradeHistoryResponse trade = httpClient.getTrade(symbol, btcusdt.getOrderId());
/*
        BinanceFuturesPositionRiskResponse positionRisk = httpClient.getPositionRisk(symbol);
*/
        System.out.println("");
    }
}