package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceApiUrlProperties;
import com.mtd.crypto.market.configuration.BinanceSecretProperties;
import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.request.*;
import com.mtd.crypto.market.data.response.*;
import com.mtd.crypto.market.exception.BinanceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@LoggableClass
public class BinanceHttpClient {

    private final RestTemplate restTemplate;
    private final BinanceSecretProperties binanceSecretProperties;
    private final BinanceApiUrlProperties binanceApiUrlProperties;

    private static final int MAX_RETRY_COUNT = 5;


    //todo burak log loglog log
    //todo burak save request and response to database or log. But do not log the signature or any secret related thing
    //TODO retry with other endpoints if request fails. api1, api2, api3


    public BinanceSystemStatusResponse getSystemStatus() {
        String url = binanceApiUrlProperties.getWalletApi() + binanceApiUrlProperties.getPath().getSystemStatus();
        BinanceSystemStatusRequestDto binanceSystemStatusRequestDto = new BinanceSystemStatusRequestDto();
        ResponseEntity<BinanceSystemStatusResponse> response = sendGetRequest(binanceSystemStatusRequestDto, url, BinanceSystemStatusResponse.class, false);
        return response.getBody();
    }

    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getPriceApi() + binanceApiUrlProperties.getPath().getPrice();
        BinanceGetPriceRequestDto binanceGetPriceRequest = new BinanceGetPriceRequestDto(symbol);
        ResponseEntity<BinanceCurrentPriceResponse> response = sendGetRequest(binanceGetPriceRequest, url, BinanceCurrentPriceResponse.class, false);
        return response.getBody().price;
    }

    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) throws JSONException {
        long endTime = System.currentTimeMillis() - interval.getMilliseconds();
        String url = binanceApiUrlProperties.getPriceApi() + binanceApiUrlProperties.getPath().getKlines();

        BinanceGetCandleRequestDto binanceGetCandleRequest = BinanceGetCandleRequestDto.builder()
                .symbol(symbol)
                .interval(interval)
                .limit(limit)
                .endTime(endTime)
                .build();

        ResponseEntity<String> response = sendGetRequest(binanceGetCandleRequest, url, String.class, false);
        return BinanceCandleStickResponse.parse(response.getBody());
    }

    //TODO burak CancelOrder
    //TODO burak get today's PNL and message to telegram

    public BinanceOrderResponse executeMarketOrder(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars) {

        BinanceMarketBuyRequestDto binanceMarketBuyRequest = BinanceMarketBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.MARKET)
                .quoteOrderQty(quantityInDollars)
                .build();

        ResponseEntity<BinanceOrderResponse> response = sendPostRequest(
                binanceMarketBuyRequest,
                binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getNormalOrder(),
                BinanceOrderResponse.class);

        return response.getBody();
    }


    public BinanceOrderResponse executeLimitOrder(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars, double price) {

        Double calculatedQuantity = calculateQuoteOrderQty(symbol, quantityInDollars);
        BinanceLimitBuyRequestDto binanceLimitBuyRequest = BinanceLimitBuyRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceOrderType.LIMIT)
                .timeInForce(BinanceOrderTimeInForce.GTC)
                .quantity(calculatedQuantity)
                .price(price)
                .build();

        ResponseEntity<BinanceOrderResponse> response = sendPostRequest(
                binanceLimitBuyRequest,
                binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getNormalOrder(),
                BinanceOrderResponse.class);

        return response.getBody();
    }


    public List<BinanceOrderResponse> getAllOpenOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, BinanceOrderResponse[].class, true);
        return Arrays.asList(response.getBody());
    }


    public List<BinanceOrderResponse> getAllOpenOrders(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(binanceGetAllOpenOrdersRequest, url, BinanceOrderResponse[].class, true);
        return Arrays.asList(response.getBody());
    }

    public BinanceOrderResponse getOrderById(String symbol, Long id) {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getNormalOrder();
        BinanceGetOrderRequestDto binanceGetOrderRequest = new BinanceGetOrderRequestDto(symbol, id);
        ResponseEntity<BinanceOrderResponse> response = sendGetRequest(binanceGetOrderRequest, url, BinanceOrderResponse.class, true);
        return response.getBody();
    }

    public List<BinanceOrderResponse> cancelAllOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOpenOrders();
        BinanceCancelAllOrdersRequestDto binanceCancelAllOrdersRequest = new BinanceCancelAllOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelAllOrdersRequest, url, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    //TODO burak handle if request is successfull but there is a problem with response object parsing.


    public BinanceNewOCOOrderResponse executeOCOSellOrder(String symbol, Integer quantityInDollars, Double sellPrice, Double stopPrice, Double limitPrice) {

        Double calculatedQuantity = calculateQuoteOrderQty(symbol, quantityInDollars);

        BinanceOcoRequestDto orderRequest = BinanceOcoRequestDto.builder()
                .symbol(symbol)
                .quantity(calculatedQuantity)
                .side(BinanceOrderSide.SELL)
                .stopPrice(stopPrice)
                .stopLimitPrice(limitPrice)
                .price(sellPrice)
                .stopLimitTimeInForce(BinanceOrderTimeInForce.GTC)
                .build();

        ResponseEntity<BinanceNewOCOOrderResponse> response = sendPostRequest(
                orderRequest,
                binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOcoOrder(),
                BinanceNewOCOOrderResponse.class);

        return response.getBody();
    }


    public BinanceCancelOCOResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOcoOrderList();

        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = BinanceCancelOCOOrderRequestDto.builder()
                .symbol(symbol)
                .orderListId(orderListId)
                .build();

        ResponseEntity<BinanceCancelOCOResponse> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, BinanceCancelOCOResponse.class);
        return response.getBody();

    }

    public List<BinanceQueryOCOResponse> getAllOpenOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOcoOpenOrderList();
        ResponseEntity<BinanceQueryOCOResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, BinanceQueryOCOResponse[].class, true);
        return Arrays.asList(response.getBody());
    }

    public List<BinanceQueryOCOResponse> getAllOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + binanceApiUrlProperties.getPath().getOcoAllOrderList();
        ResponseEntity<BinanceQueryOCOResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, BinanceQueryOCOResponse[].class, true);
        return Arrays.asList(response.getBody());
    }


    //TODO burak get OCO orders. OCO orders response is different


    private Double calculateQuoteOrderQty(String symbol, Integer quantityInDollars) {
        Double price = getPrice(symbol);
        return quantityInDollars / price;
    }


    private <T> ResponseEntity<T> sendGetRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType, boolean isSigned) {
        //todo put retry logic for 3 times with different urls

        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .params(binanceRequestBase.toMultiValueMap())
                .build();

        if (isSigned) {
            binanceRequest.signRequest(binanceSecretProperties.getSecretKey());
            binanceRequest.withApiKeyHeader(binanceSecretProperties.getApiKey());
        }

        binanceRequest.withQuery();
        return sendRequest(binanceRequest, responseType);
    }

    private <T> ResponseEntity<T> sendPostRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {

        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withRequestBody()
                .withApiKeyHeader(binanceSecretProperties.getApiKey());

        return sendRequest(binanceRequest, responseType);
    }


    private <T> ResponseEntity<T> sendDeleteRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {
        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.DELETE)
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withQuery()
                .withApiKeyHeader(binanceSecretProperties.getApiKey());

        return sendRequest(binanceRequest, responseType);
    }


    private <T> ResponseEntity<T> sendRequest(BinanceRequest binanceRequest, Class<T> responseEntity) {
        //todo burak save request and response to database
        //TODO Add retry mechanism except for the HttpClientErrorException.BadRequest. If server gives 500 or otheer than bad request retry 3 times

        try {
            log.info("--URL--  " + binanceRequest.getUrl());
            return restTemplate.exchange(binanceRequest.getUrl(), binanceRequest.getHttpMethod(), binanceRequest.getHttpEntity(), responseEntity);

        } catch (HttpClientErrorException.BadRequest e) {
            e.printStackTrace();
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        }
    }


}


