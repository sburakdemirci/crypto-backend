package com.mtd.crypto.market.client;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceApiUrlProperties;
import com.mtd.crypto.market.configuration.BinanceSecretProperties;
import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.request.*;
import com.mtd.crypto.market.data.response.BinanceCandleStickResponse;
import com.mtd.crypto.market.data.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.data.response.BinanceOcoSellResponse;
import com.mtd.crypto.market.data.response.BinanceOrderResponse;
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


    //todo burak log loglog log
    //todo burak save request and response to database or log. But do not log the signature or any secret related thing


    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getOrderApi() + "/ticker/price";
        BinanceGetPriceRequestDto binanceGetPriceRequest = new BinanceGetPriceRequestDto(symbol);
        ResponseEntity<BinanceCurrentPriceResponse> response = sendGetRequest(binanceGetPriceRequest, url, BinanceCurrentPriceResponse.class, false);
        return response.getBody().price;
    }

    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) throws JSONException {
        long endTime = System.currentTimeMillis() - interval.getMilliseconds();
        String url = binanceApiUrlProperties.getPriceApi() + "/klines";

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
                binanceApiUrlProperties.getOrderApi() + "/order",
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

        ResponseEntity<String> response = sendPostRequest(
                binanceLimitBuyRequest,
                binanceApiUrlProperties.getOrderApi() + "/order",
                String.class);

        return null;
    }


    public List<BinanceOrderResponse> getAllOpenOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, BinanceOrderResponse[].class, true);
        return Arrays.asList(response.getBody());
    }


    public List<BinanceOrderResponse> getAllOpenOrders(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(binanceGetAllOpenOrdersRequest, url, BinanceOrderResponse[].class, true);
        return Arrays.asList(response.getBody());
    }

    public BinanceOrderResponse getOrderById(String symbol, Long id) {
        String url = binanceApiUrlProperties.getOrderApi() + "/order";
        BinanceGetOrderRequestDto binanceGetOrderRequest = new BinanceGetOrderRequestDto(symbol, id);
        ResponseEntity<BinanceOrderResponse> response = sendGetRequest(binanceGetOrderRequest, url, BinanceOrderResponse.class, true);
        return response.getBody();
    }

    public List<BinanceOrderResponse> cancelAllOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        BinanceCancelAllOrdersRequestDto binanceCancelAllOrdersRequest = new BinanceCancelAllOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelAllOrdersRequest, url, BinanceOrderResponse[].class);
        return Arrays.asList(response.getBody());
    }

    //TODO burak handle if request is successfull but there is a problem with response object parsing.


    public BinanceOcoSellResponse executeOCOSellOrder(String symbol, Integer quantityInDollars, Double sellPrice, Double stopPrice, Double limitPrice) {

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

        ResponseEntity<BinanceOcoSellResponse> response = sendPostRequest(
                orderRequest,
                binanceApiUrlProperties.getOrderApi() + "/order/oco",
                BinanceOcoSellResponse.class);

        return response.getBody();
    }


    public BinanceOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceApiUrlProperties.getOrderApi() + "/orderList";

        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = BinanceCancelOCOOrderRequestDto.builder()
                .symbol(symbol)
                .orderListId(orderListId)
                .build();


        ResponseEntity<String> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, String.class);
        return null;
    }


    public BinanceOrderResponse cancelOcoOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/orderList";

        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = BinanceCancelOCOOrderRequestDto.builder()
                .symbol(symbol)
                .build();

        ResponseEntity<String> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, String.class);
        return null;
    }

    public BinanceOrderResponse getAllOpenOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, String.class, true);
        return null;
    }

    public BinanceOrderResponse getAllOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/allOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, String.class, true);
        return null;
    }


    //TODO burak get OCO orders. OCO orders response is different


    private Double calculateQuoteOrderQty(String symbol, Integer quantityInDollars) {
        Double price = getPrice(symbol);
        return quantityInDollars / price;
    }


    private <T> ResponseEntity<T> sendGetRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType, boolean isSigned) {

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
        try {
            return restTemplate.exchange(binanceRequest.getUrl(), binanceRequest.getHttpMethod(), binanceRequest.getHttpEntity(), responseEntity);

        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        }
    }
}


