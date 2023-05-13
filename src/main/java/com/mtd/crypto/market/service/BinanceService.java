package com.mtd.crypto.market.service;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final RestTemplate restTemplate;
    private final BinanceSecretProperties binanceSecretProperties;
    private final BinanceApiUrlProperties binanceApiUrlProperties;

    //todo burak log loglog log
    //todo burak save request and response to database or log. But do not log the signature or any secret related thing

    public Double getPrice(String symbol) throws BinanceException {
        String url = binanceApiUrlProperties.getOrderApi() + "/ticker/pricee";
        BinanceGetPriceRequestDto binanceGetPriceRequest = new BinanceGetPriceRequestDto(symbol);
        ResponseEntity<BinanceCurrentPriceResponse> response = sendGetRequest(binanceGetPriceRequest, url, BinanceCurrentPriceResponse.class, false);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().price;
        }
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

        ResponseEntity<BinanceOrderResponse> response = sendSignedPostRequest(
                binanceMarketBuyRequest,
                binanceApiUrlProperties.getOrderApi() + "/order",
                BinanceOrderResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to place order");
        }

        return response.getBody();
    }


    public BinanceOcoSellResponse executeOCOSellOrder(String symbol, Double quantity, Double limitPrice, Double stopPrice) {
        Double currentPrice = getPrice(symbol);

        Double calculatedQuantity = quantity / currentPrice;

        BinanceOcoRequestDto orderRequest = BinanceOcoRequestDto.builder()
                .symbol(symbol)
                .quantity(calculatedQuantity)
                .side(BinanceOrderSide.SELL)
                .stopPrice(stopPrice)
                .stopLimitPrice(calculateLimitPrice(stopPrice))
                .price(limitPrice)
                .stopLimitTimeInForce(BinanceOrderTimeInForce.GTC)
                .build();

        ResponseEntity<BinanceOcoSellResponse> response = sendSignedPostRequest(
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


    public BinanceOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol, Long orderListId) {
        String url = binanceApiUrlProperties.getOrderApi() + "/orderList";

        BinanceCancelOCOOrderRequestDto binanceCancelOCOOrderRequest = new BinanceCancelOCOOrderRequestDto(symbol, orderListId);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, BinanceOrderResponse[].class);
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


    private Double calculateLimitPrice(Double stopPrice) {
        return stopPrice - (stopPrice / 1000);
    }


    private <T> ResponseEntity<T> sendSignedPostRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {
        HttpHeaders headers = createHeaders();
        MultiValueMap<String, String> multiValueMap = binanceRequestBase.toMultiValueMap();

        multiValueMap.add("timestamp", String.valueOf(Instant.now().toEpochMilli()));
        multiValueMap.add("signature", getSignedRequest(multiValueMap));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(multiValueMap, headers);

        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
    }

    private <T> ResponseEntity<T> sendGetRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType, boolean isSigned) {
        HttpHeaders headers = createHeaders();

        MultiValueMap<String, String> multiValueMap = binanceRequestBase.toMultiValueMap();

        if (isSigned) {
            multiValueMap.add("timestamp", String.valueOf(Instant.now().toEpochMilli()));
            multiValueMap.add("signature", getSignedRequest(multiValueMap));
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                uriBuilder.queryParam(key, value);
            }
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, responseType);

        } catch (HttpClientErrorException e) {
            throw new BinanceException(e.getMessage(), e.getStatusCode());
        }
    }


    private <T> ResponseEntity<T> sendDeleteRequest(BinanceRequestBase binanceRequestBase, String url, Class<T> responseType) {
        HttpHeaders headers = createHeaders();

        MultiValueMap<String, String> multiValueMap = binanceRequestBase.toMultiValueMap();
        multiValueMap.add("timestamp", String.valueOf(Instant.now().toEpochMilli()));
        multiValueMap.add("signature", getSignedRequest(multiValueMap));


        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);

        for (Map.Entry<String, List<String>> entry : multiValueMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                uriBuilder.queryParam(key, value);
            }
        }

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.DELETE, requestEntity, responseType);
    }


    private String getSignedRequest(MultiValueMap<String, String> multiValueMap) {
        String queryString = createQueryString(multiValueMap);
        return getSignature(queryString);
    }


    private String getSignature(String queryString) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(binanceSecretProperties.getSecretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] hash = sha256_HMAC.doFinal(queryString.getBytes(StandardCharsets.UTF_8));
            return URLEncoder.encode(new String(Hex.encode(hash)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", binanceSecretProperties.getApiKey());
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }


    private static String createQueryString(MultiValueMap<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            List<String> values = params.get(key);
            for (String value : values) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(key).append("=").append(value);
            }
        }
        return sb.toString();
    }


}


