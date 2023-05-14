package com.mtd.crypto.market.client;

import com.mtd.crypto.market.client.request.BinanceRequest;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceHttpClient {

    private final RestTemplate restTemplate;
    private final BinanceSecretProperties binanceSecretProperties;
    private final BinanceApiUrlProperties binanceApiUrlProperties;
    private static String BINANCE_API_KEY_HEADER = "X-MBX-APIKEY";

    //todo burak log loglog log
    //todo burak save request and response to database or log. But do not log the signature or any secret related thing

    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getOrderApi() + "/ticker/price";
        BinanceGetPriceRequestDto binanceGetPriceRequest = new BinanceGetPriceRequestDto(symbol);
        ResponseEntity<BinanceCurrentPriceResponse> response = sendGetRequest(binanceGetPriceRequest, url, BinanceCurrentPriceResponse.class, false);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().price;
        }
        return null;
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

        if (response.getStatusCode().is2xxSuccessful()) {
            return BinanceCandleStickResponse.parse(response.getBody());
        } else {
            return null;
/*
            throw new BinanceException(String.format("Failed to fetch candles! Symbol : %s, Interval: %s, responseBody:","s","s"));
*/

        }
    }

    //TODO burak CancelOrder
    //TODO burak get today's PNL and message to telegram

    public BinanceOrderResponse executeMarketBuyOrder(String symbol, Integer quantity) {

        BinanceMarketBuyRequestDto binanceMarketBuyRequest = BinanceMarketBuyRequestDto.builder()
                .symbol(symbol)
                .side(BinanceOrderSide.BUY)
                .type(BinanceOrderType.MARKET)
                .quoteOrderQty(quantity)
                .build();

        ResponseEntity<BinanceOrderResponse> response = sendPostRequest(
                binanceMarketBuyRequest,
                binanceApiUrlProperties.getOrderApi() + "/order",
                BinanceOrderResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to place order");
        }

        return response.getBody();
    }


    public BinanceOcoSellResponse executeOCOSellOrder(String symbol, Double quantity, Double sellPrice,  Double stopPrice, Double limitPrice) {
        Double currentPrice = getPrice(symbol);

        Double calculatedQuantity = quantity / currentPrice;

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

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to place order");
        }

        return response.getBody();
    }


    public BinanceOrderResponse getAllOpenOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, BinanceOrderResponse[].class, true);
        return null;
    }


    public BinanceOrderResponse getAllOpenOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        BinanceGetAllOpenOrdersRequestDto binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(binanceGetAllOpenOrdersRequest, url, BinanceOrderResponse[].class, true);
        return null;
    }

    public BinanceOrderResponse getOrderById(String symbol, Long id) {
        String url = binanceApiUrlProperties.getOrderApi() + "/order";
        BinanceGetOrderRequestDto binanceGetOrderRequest = new BinanceGetOrderRequestDto(symbol, id);
        ResponseEntity<BinanceOrderResponse> response = sendGetRequest(binanceGetOrderRequest, url, BinanceOrderResponse.class, true);
        return null;
    }

    public BinanceOrderResponse cancelAllOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";

        BinanceCancelAllOrdersRequestDto binanceCancelAllOrdersRequest = new BinanceCancelAllOrdersRequestDto(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelAllOrdersRequest, url, BinanceOrderResponse[].class);
        return null;
    }

    //TODO burak handle if request is successfull but there is a problem with response object parsing.


    public BinanceOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceApiUrlProperties.getOrderApi() + "/orderList";

        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = new BinanceCancelOCOOrderRequestDto(symbol, orderListId);
        ResponseEntity<String> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, String.class);
        return null;
    }


    public BinanceOrderResponse getAllOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/allOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, String.class, true);
        return null;
    }


    public BinanceOrderResponse getAllOpenOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequestDto(), url, String.class, true);
        return null;
    }

    //TODO burak get OCO orders. OCO orders response is different


    //TODO burak move this to binanceCalculator
    private Double calculateLimitPrice(Double stopPrice) {
        return stopPrice - (stopPrice / 1000);
    }


    private <T> ResponseEntity<T> sendGetRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType, boolean isSigned) {

        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .httpHeaders(getHttpHeaders())
                .params(binanceRequestBase.toMultiValueMap())
                .build();

        if (isSigned) {
            binanceRequest.signRequest(binanceSecretProperties.getSecretKey());
        }

        binanceRequest.withQuery();
        return sendRequest(binanceRequest, responseType);
    }

    private <T> ResponseEntity<T> sendPostRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {

        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .httpHeaders(getHttpHeaders())
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withRequestBody();

        return sendRequest(binanceRequest, responseType);
    }


    private <T> ResponseEntity<T> sendDeleteRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {
        BinanceRequest binanceRequest = BinanceRequest.builder()
                .url(url)
                .httpMethod(HttpMethod.DELETE)
                .httpHeaders(getHttpHeaders())
                .params(binanceRequestBase.toMultiValueMap())
                .build()
                .signRequest(binanceSecretProperties.getSecretKey())
                .withQuery();

        return sendRequest(binanceRequest, responseType);
    }


    private <T> ResponseEntity<T> sendRequest(BinanceRequest binanceRequest, Class<T> responseEntity) {
        return restTemplate.exchange(binanceRequest.getUrl(), binanceRequest.getHttpMethod(), binanceRequest.getHttpEntity(), responseEntity);
    }


    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(BINANCE_API_KEY_HEADER, binanceSecretProperties.getApiKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

}


