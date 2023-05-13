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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final RestTemplate restTemplate;
    private final BinanceSecretProperties binanceSecretProperties;
    private final BinanceApiUrlProperties binanceApiUrlProperties;

    //todo burak log loglog log

    public Double getCurrentMarketPrice(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/ticker/price";
        BinanceGetPriceRequest binanceGetPriceRequest = new BinanceGetPriceRequest(symbol);

        ResponseEntity<BinanceCurrentPriceResponse> response = sendGetRequest(binanceGetPriceRequest, url, BinanceCurrentPriceResponse.class, false);
        return response.getBody().price;
    }


    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) throws JSONException {

        long endTime = System.currentTimeMillis() - (4 * 60 * 60 * 1000); // 4 hours ago

        String url = binanceApiUrlProperties.getPriceApi() + "/klines";

        BinanceGetCandleRequest binanceGetCandleRequest = BinanceGetCandleRequest.builder()
                .symbol(symbol)
                .interval(interval)
                .limit(limit)
                .endTime(endTime)
                .build();

        ResponseEntity<String> response = sendGetRequest(binanceGetCandleRequest, url, String.class, false);
        JSONArray jsonArray = new JSONArray(response.getBody());
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new HttpClientErrorException(response.getStatusCode(), "Failed to get candles data");
        } else {
            return BinanceCandleStickResponse.parse(jsonArray);
        }
    }

    //TODO burak CancelOrder
    //TODO burak get today's PNL and message to telegram
    



    public BinanceOrderResponse placeMarketBuyOrder(String symbol, Integer quantity) {

        BinanceMarketBuyRequest binanceMarketBuyRequest = BinanceMarketBuyRequest.builder()
                .symbol(symbol)
                .side(BinanceOrderSide.BUY)
                .type(BinanceOrderType.MARKET)
                .quoteOrderQty(quantity)
                .build();

        ResponseEntity<BinanceOrderResponse> response = sendSignedPostRequest(
                binanceMarketBuyRequest,
                binanceApiUrlProperties.getOrderApi() + "/order",
                HttpMethod.POST,
                BinanceOrderResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to place order");
        }

        return response.getBody();
    }


    public BinanceOcoSellResponse placeOCOSellOrder(String symbol, Double quantity, Double limitPrice, Double stopPrice) {
        Double currentPrice = getCurrentMarketPrice(symbol);

        BigDecimal calculatedQuantity = BigDecimal.valueOf(quantity / currentPrice);

        BinanceOcoRequest orderRequest = BinanceOcoRequest.builder()
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
                HttpMethod.POST,
                BinanceOcoSellResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to place order");
        }

        return response.getBody();
    }


    public BinanceOrderResponse getAllOpenOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(new BinanceGetAllOpenOrdersRequest(), url, BinanceOrderResponse[].class, true);
        return null;
    }


    public BinanceOrderResponse getAllOpenOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";
        BinanceGetAllOpenOrdersRequest binanceGetAllOpenOrdersRequest = new BinanceGetAllOpenOrdersRequest(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendGetRequest(binanceGetAllOpenOrdersRequest, url, BinanceOrderResponse[].class, true);
        return null;
    }

    public BinanceOrderResponse getOrderById(String symbol, Long id) {
        String url = binanceApiUrlProperties.getOrderApi() + "/order";
        BinanceGetOrderRequest binanceGetOrderRequest = new BinanceGetOrderRequest(symbol, id);
        ResponseEntity<BinanceOrderResponse> response = sendGetRequest(binanceGetOrderRequest, url, BinanceOrderResponse.class, true);
        return null;
    }

    public BinanceOrderResponse cancelAllOrdersBySymbol(String symbol) {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrders";

        BinanceCancelAllOrdersRequest binanceCancelAllOrdersRequest = new BinanceCancelAllOrdersRequest(symbol);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelAllOrdersRequest, url, BinanceOrderResponse[].class);
        return null;
    }


    public BinanceOrderResponse cancelOcoOrdersBySymbolAndOrderListId(String symbol,Long orderListId) {
        String url = binanceApiUrlProperties.getOrderApi() + "/orderList";

        BinanceCancelOCOOrderRequest binanceCancelOCOOrderRequest = new BinanceCancelOCOOrderRequest(symbol,orderListId);
        ResponseEntity<BinanceOrderResponse[]> response = sendDeleteRequest(binanceCancelOCOOrderRequest, url, BinanceOrderResponse[].class);
        return null;
    }


    public BinanceOrderResponse getAllOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/allOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequest(), url, String.class, true);
        return null;
    }



    public BinanceOrderResponse getAllOpenOCOOrders() {
        String url = binanceApiUrlProperties.getOrderApi() + "/openOrderList";
        ResponseEntity<String> response = sendGetRequest(new BinanceGetAllOpenOrdersRequest(), url, String.class, true);
        return null;
    }

    //TODO burak get OCO orders. OCO orders response is different




    private Double calculateLimitPrice(Double stopPrice) {
        return stopPrice - (stopPrice / 1000);
    }


    public <T> ResponseEntity<T> sendSignedPostRequest(BinanceRequest binanceRequest, String url, HttpMethod httpMethod, Class<T> responseType) {
        HttpHeaders headers = createHeaders();
        MultiValueMap<String, String> multiValueMap = binanceRequest.toMultiValueMap();

        multiValueMap.add("timestamp", String.valueOf(Instant.now().toEpochMilli()));
        multiValueMap.add("signature", getSignedRequest(multiValueMap));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(multiValueMap, headers);

        return restTemplate.exchange(url, httpMethod, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> sendGetRequest(BinanceRequest binanceRequest, String url, Class<T> responseType, boolean isSigned) {
        HttpHeaders headers = createHeaders();

        MultiValueMap<String, String> multiValueMap = binanceRequest.toMultiValueMap();

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

        return restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, requestEntity, responseType);
    }



    public <T> ResponseEntity<T> sendDeleteRequest(BinanceRequest binanceRequest, String url, Class<T> responseType) {
        HttpHeaders headers = createHeaders();

        MultiValueMap<String, String> multiValueMap = binanceRequest.toMultiValueMap();
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


