package com.mtd.crypto.market.client;


import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.configuration.BinanceFuturesApiUrlProperties;
import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesMarginType;
import com.mtd.crypto.market.data.binance.enumarator.BinanceFuturesOrderType;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.request.BinanceEmptyRequestDto;
import com.mtd.crypto.market.data.binance.request.BinanceSymbolRequestDto;
import com.mtd.crypto.market.data.binance.request.futures.BinanceAdjustLeverageRequestDto;
import com.mtd.crypto.market.data.binance.request.futures.BinanceChangeMarginTypeRequestDto;
import com.mtd.crypto.market.data.binance.request.futures.BinanceFuturesNewOrderRequestDto;
import com.mtd.crypto.market.data.binance.response.BinanceCurrentPriceResponse;
import com.mtd.crypto.market.data.binance.response.exchange.info.BinanceExchangeInfoResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceAdjustLeverageResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceChangeMarginTypeResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceFuturesOrderResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceFuturesPositionRiskResponse;
import com.mtd.crypto.market.data.binance.response.futures.BinanceMarkPriceResponse;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.exception.BinanceException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@LoggableClass
@RequiredArgsConstructor
//This is for leverage validation
@Validated
@Retryable(retryFor = BinanceException.class, maxAttempts = 3, backoff = @Backoff(delay = 100))
public class BinanceFuturesHttpClient {

    private final BinanceFuturesApiUrlProperties apiUrlProperties;
    private final BinanceHttpRequestHandler httpRequestHandler;


    @Cacheable("futuresExchangeInfo")
    public BinanceExchangeInfoResponse getExchangeInfoBySymbol(String symbol) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getExchangeInfo();
        BinanceSymbolRequestDto binanceExchangeInfoRequestDto = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceExchangeInfoRequestDto, url, false);
        ResponseEntity<BinanceExchangeInfoResponse> response = httpRequestHandler.sendRequest(binanceRequest, BinanceExchangeInfoResponse.class);
        return response.getBody();
    }


    public Double getMarkPrice(String symbol) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getMarkPrice();
        BinanceSymbolRequestDto binanceGetMarkPriceRequest = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceGetMarkPriceRequest, url, false);
        ResponseEntity<BinanceMarkPriceResponse> response = httpRequestHandler.sendRequest(binanceRequest, BinanceMarkPriceResponse.class);
        return response.getBody().getMarkPrice();
    }

    public List<BinanceMarkPriceResponse> getMarkPrices() throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getMarkPrice();
        BinanceEmptyRequestDto binanceGetMarkPricesRequest = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceGetMarkPricesRequest, url, false);
        ResponseEntity<BinanceMarkPriceResponse[]> response = httpRequestHandler.sendRequest(binanceRequest, BinanceMarkPriceResponse[].class);
        return Arrays.asList(response.getBody());
    }


    public Double getLastPrice(String symbol) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getLastPrice();
        BinanceSymbolRequestDto binanceGetLastPriceRequest = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceGetLastPriceRequest, url, false);
        ResponseEntity<BinanceCurrentPriceResponse> response = httpRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse.class);
        return response.getBody().getPrice();
    }

    public List<BinanceCurrentPriceResponse> getLastPrices() throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getLastPrice();
        BinanceEmptyRequestDto binanceGetLastPricesRequest = new BinanceEmptyRequestDto();
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceGetLastPricesRequest, url, false);
        ResponseEntity<BinanceCurrentPriceResponse[]> response = httpRequestHandler.sendRequest(binanceRequest, BinanceCurrentPriceResponse[].class);
        return Arrays.asList(response.getBody());
    }

    public BinanceFuturesOrderResponse executeMarketOrder(String symbol, BinanceOrderSide binanceOrderSide, AdjustedDecimal quantity) {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getOrder();
        BinanceFuturesNewOrderRequestDto binanceFuturesNewOrderRequestDto = BinanceFuturesNewOrderRequestDto.builder()
                .symbol(symbol)
                .side(binanceOrderSide)
                .type(BinanceFuturesOrderType.MARKET.getValue())
                .quantity(quantity)
                .build();
        BinanceRequest binanceRequest = httpRequestHandler.createPostRequest(binanceFuturesNewOrderRequestDto, url);
        ResponseEntity<BinanceFuturesOrderResponse> response = httpRequestHandler.sendRequest(binanceRequest, BinanceFuturesOrderResponse.class);
        return response.getBody();
    }


    public BinanceFuturesPositionRiskResponse getPositionRisk(String symbol) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getPositionRisk();
        BinanceSymbolRequestDto binanceGetPositionRiskDto = new BinanceSymbolRequestDto(symbol);
        BinanceRequest binanceRequest = httpRequestHandler.createGetRequest(binanceGetPositionRiskDto, url, true);
        ResponseEntity<BinanceFuturesPositionRiskResponse[]> response = httpRequestHandler.sendRequest(binanceRequest, BinanceFuturesPositionRiskResponse[].class);
        return response.getBody()[0];
    }

    public BinanceAdjustLeverageResponse adjustLeverage(String symbol, @Min(value = 1, message = "Leverage cannot be lover than 1") @Max(value = 3, message = "Leverage cannot be higher than 3") Integer leverage) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getAdjustLeverage();
        BinanceAdjustLeverageRequestDto binanceAdjustLeverageRequest = BinanceAdjustLeverageRequestDto.builder().symbol(symbol).leverage(leverage).build();
        BinanceRequest binanceRequest = httpRequestHandler.createPostRequest(binanceAdjustLeverageRequest, url);
        ResponseEntity<BinanceAdjustLeverageResponse> response = httpRequestHandler.sendRequest(binanceRequest, BinanceAdjustLeverageResponse.class);
        return response.getBody();
    }


    //TODO bunu duzenli araliklarla cagirabilirsin. Yada her order'da cagir hata alsan da no problem
    public void setMarginTypeIsolated(String symbol) throws BinanceException {
        String url = apiUrlProperties.getApi() + apiUrlProperties.getPath().getChangeMarginType();
        BinanceChangeMarginTypeRequestDto binanceChangeMarginTypeRequest = BinanceChangeMarginTypeRequestDto.builder().symbol(symbol).marginType(BinanceFuturesMarginType.ISOLATED).build();
        BinanceRequest binanceRequest = httpRequestHandler.createPostRequest(binanceChangeMarginTypeRequest, url);
        try {
            httpRequestHandler.sendRequest(binanceRequest, BinanceChangeMarginTypeResponse.class);


        } catch (BinanceException e) {
            //400 Bad Request: "{"code":-4046,"msg":"No need to change margin type."} this is safe. Because asset is already in isolated mode. Stupid error but ok"
            if (e.getMessage().contains("4046")) {
                log.info("Binance asset is already in isolated mode for symbol {}", symbol);
            } else {
                throw e;
            }

        }
    }


}
