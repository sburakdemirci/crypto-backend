package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceSpotApiUrlProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.binance.request.BinanceCancelOCOOrderRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceCancelOrderRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceEmptyRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceGetCandleRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceGetOrderRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceGetTradesRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceLimitBuyRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceMarketBuyRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceOcoRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceSymbolRequestDto;
import com.mtd.crypto.market.data.binance.response.AccountData;
import com.mtd.crypto.market.data.binance.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.data.binance.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.data.binance.response.BinanceOCOOrderResponse;
import com.mtd.crypto.market.data.binance.response.BinanceOrderResponse;
import com.mtd.crypto.market.data.binance.response.BinanceQueryOCOResponse;
import com.mtd.crypto.market.data.binance.response.BinanceSystemStatusResponse;
import com.mtd.crypto.market.data.binance.response.BinanceTradeResponse;
import com.mtd.crypto.market.data.binance.response.BinanceUserAssetResponse;
import com.mtd.crypto.market.data.binance.response.exchange.info.BinanceExchangeInfoResponse;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.exception.BinanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
@Retryable(retryFor = BinanceException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
public class BinanceSpotHttpClient {

    private final BinanceSpotApiUrlProperties binanceSpotApiUrlProperties;
    private final BinanceHttpRequestHandler binanceHttpRequestHandler;

    //todo burak save request and response to database or log. But do not log the signature or any secret related thing
    //TODO retry with other endpoints if request fails. api1, api2, api3

    //TODO add cron, if system status is not "running", message telegram
    public BinanceSystemStatusResponse getSystemStatus() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getSystemStatus();
        BinanceEmptyRequestDto binanceSystemStatusRequestDto = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceSystemStatusRequestDto, url, false);
        ResponseEntity<BinanceSystemStatusResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceSystemStatusResponse.class);
        return response.getBody();
    }


    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getPrice();
        BinanceSymbolRequestDto binanceGetPriceRequest = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetPriceRequest, url, false);
        ResponseEntity<BinanceCurrentPriceResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse.class);
        return response.getBody().getPrice();
    }

    public List<BinanceCurrentPriceResponse> getAllCoinPrices() throws BinanceException {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getPrice();
        BinanceEmptyRequestDto binanceGetAllCoinPricesRequestDto = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetAllCoinPricesRequestDto, url, false);
        ResponseEntity<BinanceCurrentPriceResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse[].class);
        return Arrays.asList(response.getBody());
    }

 /*Retry recover example. Method signatures and response types should be same for each method to use recover
   @Recover
    public List<BinanceCurrentPriceResponse> getAllCoinPricesRecover(BinanceException e)  {
        System.out.println("s");
        return null;
    }*/


    /**
     * @return All coins including users coins. It can be used for test wallet environment /api
     */
    public AccountData getAccountInfo() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getAccount();
        BinanceEmptyRequestDto binanceWalletRequest = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceWalletRequest, url, true);
        ResponseEntity<AccountData> response = binanceHttpRequestHandler.sendRequest(binanceRequest, AccountData.class);
        return response.getBody();
    }

    /**
     * @return Coins that i have in my wallet. /sapi its only available for prod
     */
    public List<BinanceUserAssetResponse> getUserAsset() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getUserAsset();
        BinanceEmptyRequestDto binanceGetUserAssetRequest = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createPostRequest(binanceGetUserAssetRequest, url);
        ResponseEntity<BinanceUserAssetResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceUserAssetResponse[].class);
        return Arrays.asList(response.getBody());
    }


    // https://testnet.binance.vision/api/v3/exchangeInfo?symbol=BTCUSDT
    @Cacheable("spotExchangeInfo")
    public BinanceExchangeInfoResponse getExchangeInfoBySymbol(String symbol) throws BinanceException {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getExchangeInfo();
        BinanceSymbolRequestDto binanceExchangeInfoRequestDto = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);
        return response.getBody();
    }


    public BinanceExchangeInfoResponse getAllExchangeInfo() throws BinanceException {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getExchangeInfo();
        BinanceEmptyRequestDto binanceAllExchangeInfoRequestDto = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceAllExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);

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
    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) {
        Long endTime = System.currentTimeMillis() - interval.getMilliseconds();
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getKlines();
        BinanceGetCandleRequestDto binanceGetCandleRequest = BinanceGetCandleRequestDto.builder()
                .symbol(symbol)
                .interval(interval)
                .limit(limit)
                .endTime(endTime)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetCandleRequest, url, false);
        ResponseEntity<String> response = binanceHttpRequestHandler.sendRequest(binanceRequest, String.class);
        return BinanceCandleStickResponse.parse(response.getBody());
    }

    public BinanceOrderResponse executeMarketOrder(String symbol, BinanceOrderSide binanceOrderSide, AdjustedDecimal quantity) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getNormalOrder();
        BinanceMarketBuyRequestDto binanceMarketBuyRequest = BinanceMarketBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.MARKET)
                .quantity(quantity)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createPostRequest(binanceMarketBuyRequest, url);
        ResponseEntity<BinanceOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public BinanceOrderResponse cancelOrderBySymbolAndOrderId(String symbol, Long orderId) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getNormalOrder();
        BinanceCancelOrderRequestDto binanceCancelOrderRequestDto = BinanceCancelOrderRequestDto.builder()
                .symbol(symbol)
                .orderId(orderId)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createDeleteRequest(binanceCancelOrderRequestDto, url);
        ResponseEntity<BinanceOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public BinanceOrderResponse executeLimitOrder(String symbol, BinanceOrderSide binanceOrderSide, AdjustedDecimal quantity, AdjustedDecimal limitPrice) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getNormalOrder();
        BinanceLimitBuyRequestDto binanceLimitBuyRequest = BinanceLimitBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.LIMIT)
                .timeInForce(BinanceOrderTimeInForce.GTC)
                .quantity(quantity)
                .price(limitPrice)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createPostRequest(binanceLimitBuyRequest, url);
        ResponseEntity<BinanceOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }

    public List<BinanceOrderResponse> getAllOpenOrders() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOpenOrders();
        BinanceSymbolRequestDto binanceGetAllOpenOrdersRequest = new BinanceSymbolRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceOrderResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public List<BinanceOrderResponse> getAllOpenOrdersBySymbol(String symbol) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOpenOrders();
        BinanceSymbolRequestDto binanceGetAllOpenOrdersRequest = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceOrderResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public BinanceOrderResponse getOrderById(String symbol, Long orderId) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getNormalOrder();
        BinanceGetOrderRequestDto binanceGetOrderRequest = new BinanceGetOrderRequestDto(symbol, orderId);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetOrderRequest, url, true);
        ResponseEntity<BinanceOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse.class);
        return response.getBody();
    }


    public List<BinanceTradeResponse> getTradesByOrderId(String symbol, Long orderId) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getMyTrades();
        BinanceGetTradesRequestDto binanceGetTradesRequestDto = new BinanceGetTradesRequestDto(symbol, orderId);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetTradesRequestDto, url, true);
        ResponseEntity<BinanceTradeResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceTradeResponse[].class);
        return Arrays.asList(response.getBody());
    }

    //TODO burak Response can be limit or oco order. Refactor this. see https://binance-docs.github.io/apidocs/spot/en/#cancel-all-open-orders-on-a-symbol-trade
    public List<BinanceOrderResponse> cancelAllOrdersBySymbol(String symbol) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOpenOrders();
        BinanceSymbolRequestDto binanceCancelAllOrdersRequest = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createDeleteRequest(binanceCancelAllOrdersRequest, url);
        ResponseEntity<BinanceOrderResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());


    }

    public BinanceOCOOrderResponse executeOCOSellOrder(String symbol, AdjustedDecimal quantity, AdjustedDecimal takeProfitPrice, AdjustedDecimal stopPrice, AdjustedDecimal limitPrice) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOcoOrder();
        BinanceOcoRequestDto orderRequest = BinanceOcoRequestDto.builder()
                .symbol(symbol)
                .quantity(quantity)
                .side(BinanceOrderSide.SELL)
                .stopPrice(stopPrice)
                .stopLimitPrice(limitPrice)
                .price(takeProfitPrice)
                .stopLimitTimeInForce(BinanceOrderTimeInForce.GTC)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createPostRequest(orderRequest, url);
        ResponseEntity<BinanceOCOOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOCOOrderResponse.class);
        return response.getBody();
    }

    public BinanceOCOOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOcoOrderList();
        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = BinanceCancelOCOOrderRequestDto.builder()
                .symbol(symbol)
                .orderListId(orderListId)
                .build();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createDeleteRequest(binanceCancelOCOOrderRequest, url);
        ResponseEntity<BinanceOCOOrderResponse> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceOCOOrderResponse.class);
        return response.getBody();
    }

    public List<BinanceQueryOCOResponse> getAllOpenOCOOrders() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOcoOpenOrderList();
        BinanceSymbolRequestDto binanceGetAllOpenOrdersRequest = new BinanceSymbolRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceQueryOCOResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceQueryOCOResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public List<BinanceQueryOCOResponse> getAllOCOOrders() {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getOcoAllOrderList();
        BinanceSymbolRequestDto binanceGetAllOpenOrdersRequest = new BinanceSymbolRequestDto();
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetAllOpenOrdersRequest, url, true);
        ResponseEntity<BinanceQueryOCOResponse[]> response = binanceHttpRequestHandler.sendRequest(binanceRequest, BinanceQueryOCOResponse[].class);
        return Arrays.asList(response.getBody());
    }

}


