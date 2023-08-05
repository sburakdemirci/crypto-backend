package com.mtd.crypto.market.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.client.BinanceSpotHttpClient;
import com.mtd.crypto.market.configuration.BinanceSpotTradeProperties;
import com.mtd.crypto.market.data.binance.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.binance.dto.BinanceDecimalInfoDto;
import com.mtd.crypto.market.data.binance.enumarator.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.binance.response.*;
import com.mtd.crypto.market.data.binance.response.exchange.info.BinanceExchangeInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@LoggableClass
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceSpotHttpClient binanceSpotHttpClient;
    private final BinanceSpotTradeProperties binanceSpotTradeProperties;

    public Double getCurrentPrice(String symbol) {
        return binanceSpotHttpClient.getPrice(Optional.of(symbol)).get(0).getPrice();
    }

    public List<BinanceCurrentPriceResponse> getAllCoinPrices() {
        return binanceSpotHttpClient.getPrice(Optional.empty());
    }

    public BinanceSystemStatusResponse getSystemStatus() {
        return binanceSpotHttpClient.getSystemStatus();
    }

    public List<BinanceUserAssetResponse> getWallet() {
        return binanceSpotHttpClient.getUserAsset();
    }

    public BinanceUserAssetResponse getBalanceBySymbol(String symbol) {
        return binanceSpotHttpClient.getUserAsset().stream().filter(asset -> asset.getAsset().equalsIgnoreCase(symbol)).findAny().orElseThrow(() -> new RuntimeException("Symbol not found int wallet"));
    }


    public AccountData getAccountInfo() {
        return binanceSpotHttpClient.getAccountInfo();
    }


    public BinanceOrderResponse executeMarketOrderWithDollar(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars) {
        Double currentPrice = binanceSpotHttpClient.getPrice(Optional.of(symbol)).get(0).getPrice();
        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);

        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(calculatedQuantity, decimalInfoDto.getQuantityStepSize());

        BinanceOrderResponse binanceOrderResponse = binanceSpotHttpClient.executeMarketOrder(symbol, binanceOrderSide, adjustedQuantity);
        binanceOrderResponse.setCommission(getUSDTCommission(binanceOrderResponse));
        return binanceOrderResponse;
    }


    public BinanceOrderResponse executeMarketOrderWithQuantity(String symbol, BinanceOrderSide binanceOrderSide, Double quantity) {

        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(quantity, decimalInfoDto.getQuantityStepSize());

        BinanceOrderResponse binanceOrderResponse = binanceSpotHttpClient.executeMarketOrder(symbol, binanceOrderSide, adjustedQuantity);
        binanceOrderResponse.setCommission(getUSDTCommission(binanceOrderResponse));
        return binanceOrderResponse;
    }


    /**
     * If commission asset includes USD for example its USDT or BUSD etc. It will count as a commission
     * If commission asset is not in dollars, it will fetch the price and calculate price in dollars.
     *
     * @param binanceOrderResponse
     * @return
     */
    private double getUSDTCommission(BinanceOrderResponse binanceOrderResponse) {
        return binanceOrderResponse.getFills().stream().map(fill -> {
            if (!fill.getCommissionAsset().contains("USD")) {
                return binanceSpotHttpClient.getPrice(Optional.of(fill.getCommissionAsset() + "USDT")).get(0).getPrice() * fill.getCommission();
            } else {
                return fill.getCommission();
            }
        }).mapToDouble(d -> d).sum();
    }


    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) {
        return binanceSpotHttpClient.getCandles(symbol, interval, limit);
    }

    public BinanceOrderResponse getOrderById(String symbol, Long orderId) {
        return binanceSpotHttpClient.getOrderById(symbol, orderId);
    }

    public List<BinanceTradeResponse> getTradesByOrderId(String symbol, Long orderId) {
        return binanceSpotHttpClient.getTradesByOrderId(symbol, orderId);
    }

    public BinanceDecimalInfoDto getDecimalInfo(String symbol) {
        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceSpotHttpClient.getExchangeInfoBySymbol(symbol);
        return BinanceDecimalInfoDto.builder()
                .priceTickSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize())
                .quantityStepSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getLotSizeFilter().getStepSize())
                .minimumOrderInDollars(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getNotionalFilter().getMinNotional())
                .build();
    }

    public void executeHealthCheck() {
        BinanceOrderResponse binanceOrderResponse = executeMarketOrderWithDollar("BTCUSDT", BinanceOrderSide.BUY, 15);
        executeMarketOrderWithQuantity("BTCUSDT", BinanceOrderSide.SELL, binanceOrderResponse.getExecutedQty());
    }


    private Double convertQuantity(Integer quantityInDollars, Double currentPrice) {
        return quantityInDollars / currentPrice;
    }

}
