package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceApiUrlProperties;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderType;
import com.mtd.crypto.market.data.request.*;
import com.mtd.crypto.market.data.response.*;
import com.mtd.crypto.market.data.response.exchange.info.BinanceExchangeInfoResponse;
import com.mtd.crypto.market.exception.BinanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
public class BinanceHttpClient {

    private final BinanceApiUrlProperties binanceApiUrlProperties;
    private final BinanceRequestHandler binanceRequestHandler;

    //todo burak save request and response to database or log. But do not log the signature or any secret related thing
    //TODO retry with other endpoints if request fails. api1, api2, api3

    //TODO add cron, if system status is not "running", message telegram
    public BinanceSystemStatusResponse getSystemStatus() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getSystemStatus();
        BinanceSystemStatusRequestDto binanceSystemStatusRequestDto = new BinanceSystemStatusRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceSystemStatusRequestDto, url, false);
        ResponseEntity<BinanceSystemStatusResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceSystemStatusResponse.class);
        return response.getBody();
    }

    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getPrice();
        BinanceGetPriceRequestDto binanceGetPriceRequest = new BinanceGetPriceRequestDto(symbol);
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetPriceRequest, url, false);
        ResponseEntity<BinanceCurrentPriceResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse.class);
        return response.getBody().getPrice();
    }

    public List<BinanceCurrentPriceResponse> getAllCoinPrices() throws BinanceException {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getPrice();
        BinanceGetAllCoinPricesRequestDto binanceGetAllCoinPricesRequestDto = new BinanceGetAllCoinPricesRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetAllCoinPricesRequestDto, url, false);
        ResponseEntity<BinanceCurrentPriceResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse[].class);
        return Arrays.asList(response.getBody());
    }


    /**
     * @return All coins including users coins. It can be used for test wallet environment /api
     */
    public AccountData getAccountInfo() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getAccount();
        BinanceWalletRequest binanceWalletRequest = new BinanceWalletRequest();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceWalletRequest, url, true);
        ResponseEntity<AccountData> response = binanceRequestHandler.sendRequest(binanceRequest, AccountData.class);
        return response.getBody();
    }

    /**
     * @return Coins that i have in my wallet. /sapi its only available for prod
     */
    public List<UserAssetResponse> getUserAsset() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getUserAsset();
        BinanceGetUserAssetRequest binanceGetUserAssetRequest = new BinanceGetUserAssetRequest();
        BinanceRequest binanceRequest = binanceRequestHandler.createPostRequest(binanceGetUserAssetRequest, url);
        ResponseEntity<UserAssetResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, UserAssetResponse[].class);
        return Arrays.asList(response.getBody());
    }


    // https://testnet.binance.vision/api/v3/exchangeInfo?symbol=BTCUSDT
    @Cacheable("exchangeInfo")
    public BinanceExchangeInfoResponse getExchangeInfoBySymbol(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getExchangeInfo();
        BinanceExchangeInfoRequestDto binanceExchangeInfoRequestDto = new BinanceExchangeInfoRequestDto(symbol);
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);
        return response.getBody();
    }


    public BinanceExchangeInfoResponse getAllExchangeInfo() throws BinanceException {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getExchangeInfo();
        BinanceAllExchangeInfoRequestDto binanceAllExchangeInfoRequestDto = new BinanceAllExchangeInfoRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceAllExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);

        return response.getBody();
    }


/*    public BinanceExchangeInfoResponse getExchangeInfoAllSymbols() throws BinanceException {
        String url = "https://api.binance.com/api/v3/exchangeInfo";

        BinanceSystemStatusRequestDto binanceExchangeInfoRequestDto = new BinanceSystemStatusRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);
        return response.getBody();
    }*/


    /**
     * @param symbol
     * @param interval
     * @param limit    = How many candles.
     * @return
     */
    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit)  {
        Long endTime = System.currentTimeMillis() - interval.getMilliseconds();
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getKlines();
        BinanceGetCandleRequestDto binanceGetCandleRequest = BinanceGetCandleRequestDto.builder()
                .symbol(symbol)
                .interval(interval)
                .limit(limit)
                .endTime(endTime)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetCandleRequest, url, false);
        ResponseEntity<String> response = binanceRequestHandler.sendRequest(binanceRequest, String.class);
        return BinanceCandleStickResponse.parse(response.getBody());
    }

    public BinanceOrderResponse executeMarketOrder(String symbol, BinanceOrderSide binanceOrderSide, AdjustedDecimal quantity) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getNormalOrder();
        BinanceMarketBuyRequestDto binanceMarketBuyRequest = BinanceMarketBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.MARKET)
                .quantity(quantity)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createPostRequest(binanceMarketBuyRequest, url);
        ResponseEntity<BinanceOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public BinanceOrderResponse cancelOrderBySymbolAndOrderId(String symbol, Long orderId) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getNormalOrder();
        BinanceCancelOrderRequestDto binanceCancelOrderRequestDto = BinanceCancelOrderRequestDto.builder()
                .symbol(symbol)
                .orderId(orderId)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createDeleteRequest(binanceCancelOrderRequestDto, url);
        ResponseEntity<BinanceOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public BinanceOrderResponse executeLimitOrder(String symbol, BinanceOrderSide binanceOrderSide, AdjustedDecimal quantity, AdjustedDecimal limitPrice) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getNormalOrder();
        BinanceLimitBuyRequestDto binanceLimitBuyRequest = BinanceLimitBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.LIMIT)
                .timeInForce(BinanceOrderTimeInForce.GTC)
                .quantity(quantity)
                .price(limitPrice)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createPostRequest(binanceLimitBuyRequest, url);
        ResponseEntity<BinanceOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public List<BinanceOrderResponse> getAllOpenOrders() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceOrderResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public List<BinanceOrderResponse> getAllOpenOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto(symbol);
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceOrderResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public BinanceOrderResponse getOrderById(String symbol, Long orderId) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getNormalOrder();
        BinanceGetOrderRequestDto binanceGetOrderRequest = new BinanceGetOrderRequestDto(symbol, orderId);
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetOrderRequest, url, true);
        ResponseEntity<BinanceOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }


    public List<BinanceTradeResponse> getTradesByOrderId(String symbol, Long orderId) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getMyTrades();
        BinanceGetTradesRequestDto binanceGetTradesRequestDto = new BinanceGetTradesRequestDto(symbol, orderId);
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetTradesRequestDto, url, true);
        ResponseEntity<BinanceTradeResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceTradeResponse[].class);
        return Arrays.asList(response.getBody());
    }

    //TODO burak Response can be limit or oco order. Refactor this. see https://binance-docs.github.io/apidocs/spot/en/#cancel-all-open-orders-on-a-symbol-trade
    public List<BinanceOrderResponse> cancelAllOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        BinanceCancelAllOrdersRequestDto binanceCancelAllOrdersRequest = new BinanceCancelAllOrdersRequestDto(symbol);
        BinanceRequest binanceRequest = binanceRequestHandler.createDeleteRequest(binanceCancelAllOrdersRequest, url);
        ResponseEntity<BinanceOrderResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());


    }

    public BinanceOCOOrderResponse executeOCOSellOrder(String symbol, AdjustedDecimal quantity, AdjustedDecimal takeProfitPrice, AdjustedDecimal stopPrice, AdjustedDecimal limitPrice) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOcoOrder();
        BinanceOcoRequestDto orderRequest = BinanceOcoRequestDto.builder()
                .symbol(symbol)
                .quantity(quantity)
                .side(BinanceOrderSide.SELL)
                .stopPrice(stopPrice)
                .stopLimitPrice(limitPrice)
                .price(takeProfitPrice)
                .stopLimitTimeInForce(BinanceOrderTimeInForce.GTC)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createPostRequest(orderRequest, url);
        ResponseEntity<BinanceOCOOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOCOOrderResponse.class);
        return response.getBody();
    }

    public BinanceOCOOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOcoOrderList();
        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = BinanceCancelOCOOrderRequestDto.builder()
                .symbol(symbol)
                .orderListId(orderListId)
                .build();
        BinanceRequest binanceRequest = binanceRequestHandler.createDeleteRequest(binanceCancelOCOOrderRequest, url);
        ResponseEntity<BinanceOCOOrderResponse> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceOCOOrderResponse.class);
        return response.getBody();
    }

    public List<BinanceQueryOCOResponse> getAllOpenOCOOrders() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOcoOpenOrderList();
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceQueryOCOResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceQueryOCOResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public List<BinanceQueryOCOResponse> getAllOCOOrders() {
        String url = binanceApiUrlProperties.getApi() + binanceApiUrlProperties.getPath().getOcoAllOrderList();
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto();
        BinanceRequest binanceRequest = binanceRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceQueryOCOResponse[]> response = binanceRequestHandler.sendRequest(binanceRequest, BinanceQueryOCOResponse[].class);
        return Arrays.asList(response.getBody());
    }

}


