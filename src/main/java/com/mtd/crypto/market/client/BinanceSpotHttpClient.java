package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceSpotApiUrlProperties;
import com.mtd.crypto.market.data.binance.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.binance.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.binance.request.*;
import com.mtd.crypto.market.data.binance.response.*;
import com.mtd.crypto.market.data.binance.response.exchange.info.BinanceExchangeInfoResponse;
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
import java.util.Optional;

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


    public List<BinanceCurrentPriceResponse> getPrice(Optional<String> symbol) throws BinanceException {
        String url = binanceSpotApiUrlProperties.getApi() + binanceSpotApiUrlProperties.getPath().getPrice();
        BinanceSymbolRequestDto binanceGetPriceRequest = new BinanceSymbolRequestDto(symbol.orElse(null));
        BinanceRequest binanceRequest = binanceHttpRequestHandler.createGetRequest(binanceGetPriceRequest, url, false);
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

}


