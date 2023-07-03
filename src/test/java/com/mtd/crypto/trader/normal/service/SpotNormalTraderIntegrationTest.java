package com.mtd.crypto.trader.normal.service;


import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.configuration.SpotNormalTradingStrategyConfiguration;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.helper.SpotNormalTradePriceCalculatorHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("dev")
public class SpotNormalTraderIntegrationTest {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0")).withDatabaseName("cryptotest")
            .withUsername("root")
            .withPassword("test");

    @Autowired
    SpotNormalTraderProxyService proxyService;

    @Autowired
    SpotNormalTradeDataService dataService;

    @Autowired
    SpotNormalTradingStrategyConfiguration spotNormalTradingStrategyConfiguration;

    @MockBean
    private BinanceService binanceService;

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.jpa.properties.hibernate.enable_lazy_load_no_trans", () -> true);
    }

    private static final Double TEST_ENTRY_PRICE = 100.0;
    private static final Double TEST_BINANCE_MARKET_ENTRY_PRICE = 100.1;
    private static final Double TEST_TAKE_PROFIT_PRICE = 110.0;
    private static final Double TEST_STOP_PRICE = 96.0;
    private static final String TEST_SYMBOL = "BTCUSTD";
    private static final String TEST_BASE_SYMBOL = "USDT";
    private static final Double WALLET_AMOUNT = 1500.0;
    private static final Integer TRADE_WALLET_PERCENTAGE = 20;


    @Test
    public void testHappyPathNonPriceDrop() throws JSONException {

        //creating trade data
        SpotNormalTradeDto spotNormalTradeDto = createTradeData(false);
        SpotNormalTradeData tradeData = dataService.createTradeData(spotNormalTradeDto);
        assertNotNull(tradeData.getCreatedTime());

        dataService.approveTrade(tradeData.getId());

        //Construct case
        BinanceCandleStickResponse validEntryCandleStickResponse = new BinanceCandleStickResponse();
        validEntryCandleStickResponse.setClose(100.0);

        when(binanceService.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, 1)).thenReturn(Collections.singletonList(validEntryCandleStickResponse));
        when(binanceService.getBalanceBySymbol(TEST_BASE_SYMBOL)).thenReturn(WALLET_AMOUNT);


        //Enter Position
        when(binanceService.executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300))
                .thenReturn(createBinanceMarketOrder(2.9970, 300.0, BinanceOrderSide.BUY));
        proxyService.checkAndEnterPositions(); //Note: AVG entry price 101.1
        verify(binanceService).executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300);
        reset(binanceService);


        //Check the data after entering the position
        SpotNormalTradeData tradeDataAfterEnter = dataService.findById(tradeData.getId());
        assertEquals(TradeStatus.IN_POSITION, tradeDataAfterEnter.getTradeStatus());
        assertNotNull(tradeDataAfterEnter.getPositionStartedAt());
        assertTrue(tradeDataAfterEnter.getAverageEntryPrice() > 0.0);
        assertTrue(tradeDataAfterEnter.getTotalQuantity() > 0.0);
        assertTrue(tradeDataAfterEnter.getQuantityLeftInPosition() > 0.0);

        //First Partial Exit
        SpotNormalTradeData tradeBeforeFirstPartialExit = dataService.findById(tradeData.getId());
        Double firstPartialExit = SpotNormalTradePriceCalculatorHelper.calculateFirstPartialExitPrice(tradeBeforeFirstPartialExit, spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep());
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(firstPartialExit);
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3)).thenReturn(createBinanceMarketOrder(tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3, firstPartialExit * tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3, BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3);
        reset(binanceService);

        //Second Partial Exit
        SpotNormalTradeData tradeBeforeSecondPartialExit = dataService.findById(tradeData.getId());
        Double secondPartialExitPrice = SpotNormalTradePriceCalculatorHelper.calculateSecondPartialExitPrice(tradeBeforeSecondPartialExit, spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep());
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(secondPartialExitPrice);
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeSecondPartialExit.getQuantityLeftInPosition() / 2)).thenReturn(createBinanceMarketOrder(tradeBeforeSecondPartialExit.getQuantityLeftInPosition() / 2, secondPartialExitPrice * tradeBeforeSecondPartialExit.getQuantityLeftInPosition() / 2, BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeSecondPartialExit.getQuantityLeftInPosition() / 2);
        reset(binanceService);


        //All Profit Exit Sale
        SpotNormalTradeData tradeBeforeAllProfitExit = dataService.findById(tradeData.getId());
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(tradeBeforeAllProfitExit.getTakeProfit());
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeAllProfitExit.getQuantityLeftInPosition())).thenReturn(createBinanceMarketOrder(tradeBeforeAllProfitExit.getQuantityLeftInPosition(), tradeBeforeAllProfitExit.getTakeProfit() * tradeBeforeAllProfitExit.getQuantityLeftInPosition(), BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeAllProfitExit.getQuantityLeftInPosition());


        //Verify the whole position after finish
        SpotNormalTradeData tradeDataFinished = dataService.findById(tradeData.getId());
        List<SpotNormalTradeMarketOrder> marketOrdersFinished = dataService.findMarketOrderByParentTrade(tradeData.getId());

        List<SpotNormalTradeMarketOrder> profitSellOrders = marketOrdersFinished.stream()
                .filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.SELL)
                .collect(Collectors.toList());

        assertEquals(3, profitSellOrders.size());

        double sumOfDollarsForMarketSells = profitSellOrders.stream().map(marketOrder -> marketOrder.getAveragePrice() * marketOrder.getQuantity()).mapToDouble(d -> d).sum();
        assertTrue(sumOfDollarsForMarketSells > 300);
        assertEquals(TradeStatus.POSITION_FINISHED_WITH_PROFIT, tradeDataFinished.getTradeStatus());
        assertNotNull(tradeDataFinished.getPositionFinishedAt());
    }


    @Test
    public void testStopAfterEnter() throws JSONException {

        //creating trade data
        SpotNormalTradeDto spotNormalTradeDto = createTradeData(false);
        SpotNormalTradeData tradeData = dataService.createTradeData(spotNormalTradeDto);
        assertNotNull(tradeData.getCreatedTime());

        dataService.approveTrade(tradeData.getId());

        //Construct case
        BinanceCandleStickResponse validEntryCandleStickResponse = new BinanceCandleStickResponse();
        validEntryCandleStickResponse.setClose(100.0);

        when(binanceService.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, 1)).thenReturn(Collections.singletonList(validEntryCandleStickResponse));
        when(binanceService.getBalanceBySymbol(TEST_BASE_SYMBOL)).thenReturn(WALLET_AMOUNT);


        //Enter Position
        when(binanceService.executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300))
                .thenReturn(createBinanceMarketOrder(2.9970, 300.0, BinanceOrderSide.BUY));
        proxyService.checkAndEnterPositions(); //Note: AVG entry price 101.1
        verify(binanceService).executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300);
        reset(binanceService);


        //Check the data after entering the position
        SpotNormalTradeData tradeDataAfterEnter = dataService.findById(tradeData.getId());
        assertEquals(TradeStatus.IN_POSITION, tradeDataAfterEnter.getTradeStatus());
        assertNotNull(tradeDataAfterEnter.getPositionStartedAt());
        assertTrue(tradeDataAfterEnter.getAverageEntryPrice() > 0.0);
        assertTrue(tradeDataAfterEnter.getTotalQuantity() > 0.0);
        assertTrue(tradeDataAfterEnter.getQuantityLeftInPosition() > 0.0);


        //All Stop Loss Exit Sale
        SpotNormalTradeData tradeBeforeStopLossExit = dataService.findById(tradeData.getId());
        Double coinPriceToStop = tradeBeforeStopLossExit.getStop() * 0.999;
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(coinPriceToStop);
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeStopLossExit.getQuantityLeftInPosition())).thenReturn(createBinanceMarketOrder(tradeBeforeStopLossExit.getQuantityLeftInPosition(), coinPriceToStop * tradeBeforeStopLossExit.getQuantityLeftInPosition(), BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeStopLossExit.getQuantityLeftInPosition());


        //Verify the whole position after finish
        SpotNormalTradeData tradeDataFinished = dataService.findById(tradeData.getId());
        List<SpotNormalTradeMarketOrder> marketOrdersFinished = dataService.findMarketOrderByParentTrade(tradeData.getId());

        assertEquals(2, marketOrdersFinished.size());

        Optional<SpotNormalTradeMarketOrder> first = marketOrdersFinished.stream().filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.SELL).findFirst();
        assertTrue(first.isPresent());

        assertTrue(first.get().getQuantity() * first.get().getAveragePrice() < 300);
        assertEquals(TradeStatus.POSITION_FINISHED_WITH_LOSS, tradeDataFinished.getTradeStatus());
        assertNotNull(tradeDataFinished.getPositionFinishedAt());
    }


    @Test
    public void testMakeStopEntryPriceAfterFirstProfitSale() throws JSONException {

        //creating trade data
        SpotNormalTradeDto spotNormalTradeDto = createTradeData(false);
        SpotNormalTradeData tradeData = dataService.createTradeData(spotNormalTradeDto);
        assertNotNull(tradeData.getCreatedTime());

        dataService.approveTrade(tradeData.getId());

        //Construct case
        BinanceCandleStickResponse validEntryCandleStickResponse = new BinanceCandleStickResponse();
        validEntryCandleStickResponse.setClose(100.0);

        when(binanceService.getCandles(TEST_SYMBOL, BinanceCandleStickInterval.FOUR_HOURS, 1)).thenReturn(Collections.singletonList(validEntryCandleStickResponse));
        when(binanceService.getBalanceBySymbol(TEST_BASE_SYMBOL)).thenReturn(WALLET_AMOUNT);


        //Enter Position
        when(binanceService.executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300))
                .thenReturn(createBinanceMarketOrder(2.9970, 300.0, BinanceOrderSide.BUY));
        proxyService.checkAndEnterPositions(); //Note: AVG entry price 101.1
        verify(binanceService).executeMarketOrderWithDollar(TEST_SYMBOL, BinanceOrderSide.BUY, 300);
        reset(binanceService);


        //Check the data after entering the position
        SpotNormalTradeData tradeDataAfterEnter = dataService.findById(tradeData.getId());
        assertEquals(TradeStatus.IN_POSITION, tradeDataAfterEnter.getTradeStatus());
        assertNotNull(tradeDataAfterEnter.getPositionStartedAt());
        assertTrue(tradeDataAfterEnter.getAverageEntryPrice() > 0.0);
        assertTrue(tradeDataAfterEnter.getTotalQuantity() > 0.0);
        assertTrue(tradeDataAfterEnter.getQuantityLeftInPosition() > 0.0);

        //First Partial Exit
        SpotNormalTradeData tradeBeforeFirstPartialExit = dataService.findById(tradeData.getId());
        Double firstPartialExit = SpotNormalTradePriceCalculatorHelper.calculateFirstPartialExitPrice(tradeBeforeFirstPartialExit, spotNormalTradingStrategyConfiguration.getPartialExitPercentageStep());
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(firstPartialExit);
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3)).thenReturn(createBinanceMarketOrder(tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3, firstPartialExit * tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3, BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeFirstPartialExit.getQuantityLeftInPosition() / 3);
        reset(binanceService);


        //All Stop Loss Exit Sale With increased stop to average entry price
        SpotNormalTradeData tradeBeforeStopLossExit = dataService.findById(tradeData.getId());
        Double coinPriceToStop = tradeBeforeStopLossExit.getAverageEntryPrice() * 0.999;
        when(binanceService.getCurrentPrice(TEST_SYMBOL)).thenReturn(coinPriceToStop);
        when(binanceService.executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeStopLossExit.getQuantityLeftInPosition())).thenReturn(createBinanceMarketOrder(tradeBeforeStopLossExit.getQuantityLeftInPosition(), coinPriceToStop * tradeBeforeStopLossExit.getQuantityLeftInPosition(), BinanceOrderSide.SELL));
        proxyService.checkProfitOrExitPosition();
        verify(binanceService).executeMarketOrderWithQuantity(TEST_SYMBOL, BinanceOrderSide.SELL, tradeBeforeStopLossExit.getQuantityLeftInPosition());


        //Verify the whole position after finish
        SpotNormalTradeData tradeDataFinished = dataService.findById(tradeData.getId());
        List<SpotNormalTradeMarketOrder> marketOrdersFinished = dataService.findMarketOrderByParentTrade(tradeData.getId());

        List<SpotNormalTradeMarketOrder> sellOrders = marketOrdersFinished.stream()
                .filter(marketOrder -> marketOrder.getSide() == BinanceOrderSide.SELL)
                .collect(Collectors.toList());

        assertEquals(2, sellOrders.size());

        double sumOfDollarsForMarketSells = sellOrders.stream().map(marketOrder -> marketOrder.getAveragePrice() * marketOrder.getQuantity()).mapToDouble(d -> d).sum();
        assertTrue(sumOfDollarsForMarketSells > 300);
        assertEquals(TradeStatus.POSITION_FINISHED_WITH_PROFIT, tradeDataFinished.getTradeStatus());
        assertNotNull(tradeDataFinished.getPositionFinishedAt());
    }

    private SpotNormalTradeDto createTradeData(boolean isPriceDrop) {
        return SpotNormalTradeDto
                .builder()
                .symbol(TEST_SYMBOL)
                .baseTradingSymbol(TEST_BASE_SYMBOL)
                .entry(TEST_ENTRY_PRICE)
                .takeProfit(TEST_TAKE_PROFIT_PRICE)
                .stop(TEST_STOP_PRICE)
                .walletPercentage(TRADE_WALLET_PERCENTAGE)
                .isPriceDropRequired(isPriceDrop)
                .source(TradeSource.BURAK)
                .build();
    }

    private BinanceOrderResponse createBinanceMarketOrder(Double coinAmount, Double dollarAmount, BinanceOrderSide side) {
        BinanceOrderResponse binanceOrderResponse = new BinanceOrderResponse();
        binanceOrderResponse.setOrderId(ThreadLocalRandom.current().nextLong());
        binanceOrderResponse.setCummulativeQuoteQty(dollarAmount);
        binanceOrderResponse.setExecutedQty(coinAmount);
        binanceOrderResponse.setSide(side);
        return binanceOrderResponse;
    }

}
