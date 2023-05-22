package com.mtd.crypto.market.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.dto.BinanceDecimalInfoDto;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.*;
import com.mtd.crypto.market.data.response.exchange.info.BinanceExchangeInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.util.List;


@LoggableClass
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceHttpClient binanceHttpClient;

    public Double getCurrentPrice(String symbol) {
        return binanceHttpClient.getPrice(symbol);
    }

    public BinanceSystemStatusResponse getSystemStatus() {
        return binanceHttpClient.getSystemStatus();
    }

    //TODO test all the logic. Test it with given tick and quantity step prices. Make sure price is adjusting with correct values for AdjustedDecimal objects

    //TODO test getExchangeInfo method cache.
    public BinanceOCOOrderResponse executeOcoSellOrder(String symbol, Integer quantityInDollars, Double takeProfitPrice, Double stopPrice) {
        Double currentPrice = binanceHttpClient.getPrice(symbol);
        BinanceDecimalInfoDto decimalInfo = getDecimalInfo(symbol);


        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        Double calculatedLimitPrice = calculateStopLimitPrice(stopPrice, decimalInfo.getPriceTickSize());

        AdjustedDecimal quantity = new AdjustedDecimal(calculatedQuantity, decimalInfo.getQuantityStepSize());

        AdjustedDecimal adjustedTakeProfit = new AdjustedDecimal(takeProfitPrice, decimalInfo.getPriceTickSize());
        AdjustedDecimal adjustedStop = new AdjustedDecimal(stopPrice, decimalInfo.getPriceTickSize());
        AdjustedDecimal adjustedLimit = new AdjustedDecimal(calculatedLimitPrice, decimalInfo.getPriceTickSize());

        return binanceHttpClient.executeOCOSellOrder(symbol, quantity, adjustedTakeProfit, adjustedStop, adjustedLimit);
    }


    public BinanceOrderResponse executeMarketOrder(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars) {
        Double currentPrice = binanceHttpClient.getPrice(symbol);
        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);

        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(calculatedQuantity, decimalInfoDto.getQuantityStepSize());
        return binanceHttpClient.executeMarketOrder(symbol, binanceOrderSide, adjustedQuantity);
    }


    //TODO BURAK mark them as @Transactional if necessary
    public BinanceOrderResponse executeLimitOrder(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars, Double limitPrice) {
        Double currentPrice = binanceHttpClient.getPrice(symbol);
        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);

        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(calculatedQuantity, decimalInfoDto.getQuantityStepSize());
        AdjustedDecimal adjustedLimitPrice = new AdjustedDecimal(limitPrice, decimalInfoDto.getPriceTickSize());

        return binanceHttpClient.executeLimitOrder(symbol, binanceOrderSide, adjustedQuantity, adjustedLimitPrice);
    }

    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit) throws JSONException {
        return binanceHttpClient.getCandles(symbol, interval, limit);
    }


    public BinanceOrderResponse cancelOrderBySymbolAndOrderId(String symbol, Long orderId) {
        return binanceHttpClient.cancelOrderBySymbolAndOrderId(symbol, orderId);
    }


    public List<BinanceOrderResponse> getAllOpenOrders() {
        return binanceHttpClient.getAllOpenOrders();
    }

    public List<BinanceOrderResponse> getAllOpenOrdersBySymbol(String symbol) {
        return binanceHttpClient.getAllOpenOrdersBySymbol(symbol);
    }


    public BinanceOrderResponse getOrderById(String symbol, Long orderId) {
        return binanceHttpClient.getOrderById(symbol, orderId);
    }

/*    public List<BinanceOrderResponse> cancelAllOrdersBySymbol(String symbol) {
        return binanceHttpClient.cancelAllOrdersBySymbol(symbol);
    }*/

    public BinanceOCOOrderResponse cancelOcoOrderBySymbolAndOrderListId(String symbol, Long orderListId) {
        return binanceHttpClient.cancelOcoOrdersBySymbolAndOrderListId(symbol, orderListId);
    }

    public List<BinanceQueryOCOResponse> getAllOpenOCOOrders() {
        return binanceHttpClient.getAllOpenOCOOrders();
    }

    public List<BinanceQueryOCOResponse> getAllOCOOrders() {
        return binanceHttpClient.getAllOCOOrders();
    }


    public BinanceDecimalInfoDto getDecimalInfo(String symbol) {
        BinanceExchangeInfoResponse exchangeInfoBySymbol = binanceHttpClient.getExchangeInfoBySymbol(symbol);
        return BinanceDecimalInfoDto.builder()
                .priceTickSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getPriceFilter().getTickSize())
                .quantityStepSize(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getLotSizeFilter().getStepSize())
                .minimumOrderInDollars(exchangeInfoBySymbol.getSymbols().get(0).getBinanceFilter().getNotionalFilter().getMinNotional())
                .build();
    }


    private Double convertQuantity(Integer quantityInDollars, Double currentPrice) {
        return quantityInDollars / currentPrice;
    }


    private Double calculateStopLimitPrice(Double stopPrice, Double tickPrice) {
        return stopPrice - tickPrice;
    }

    private void checkMinimumOrder(Integer quantityInDollars, Double mininumOrderInDollars) {
        if (quantityInDollars < mininumOrderInDollars) {
            throw new RuntimeException("Order size cannot be lower than minimum order size for symbol");
        }
    }


}
