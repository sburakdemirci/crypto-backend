package com.mtd.crypto.market.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.client.BinanceHttpClient;
import com.mtd.crypto.market.configuration.BinanceTradeProperties;
import com.mtd.crypto.market.data.custom.AdjustedDecimal;
import com.mtd.crypto.market.data.dto.BinanceDecimalInfoDto;
import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.market.data.response.*;
import com.mtd.crypto.market.data.response.exchange.info.BinanceExchangeInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;


@LoggableClass
@Service
@RequiredArgsConstructor
public class BinanceService {

    private final BinanceHttpClient binanceHttpClient;
    private final BinanceTradeProperties binanceTradeProperties;

    public Double getCurrentPrice(String symbol) {
        return binanceHttpClient.getPrice(symbol);
    }

    public List<BinanceCurrentPriceResponse> getAllCoinPrices() {
        return binanceHttpClient.getAllCoinPrices();
    }

    public BinanceSystemStatusResponse getSystemStatus() {
        return binanceHttpClient.getSystemStatus();
    }

    public List<UserAssetResponse> getUserAsset() {
        return binanceHttpClient.getUserAsset();
    }

    public AccountData getAccountInfo() {
        return binanceHttpClient.getAccountInfo();
    }

    public Double getBalanceBySymbol(String symbol) {
        AccountData accountInfo = binanceHttpClient.getAccountInfo();
        return accountInfo.getBalances().stream().filter(balances -> balances.getAsset().equalsIgnoreCase(symbol)).findFirst().orElseThrow(() -> new RuntimeException("Asset could not found in the wallet")).getFree();
    }

    //TODO test all the logic. Test it with given tick and quantity step prices. Make sure price is adjusting with correct values for AdjustedDecimal objects

    //TODO test getExchangeInfo method cache.
    public BinanceOCOOrderResponse executeOcoSellOrder(String symbol, Double quantity, Double takeProfitPrice, Double stopPrice) {

        Double currentPrice = binanceHttpClient.getPrice(symbol);
        BinanceDecimalInfoDto decimalInfo = getDecimalInfo(symbol);


        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(quantity, decimalInfo.getQuantityStepSize());
        Double calculatedLimitPrice = calculateStopLimitPrice(stopPrice, decimalInfo.getPriceTickSize());
        AdjustedDecimal adjustedTakeProfit = new AdjustedDecimal(takeProfitPrice, decimalInfo.getPriceTickSize());
        AdjustedDecimal adjustedStop = new AdjustedDecimal(stopPrice, decimalInfo.getPriceTickSize());
        AdjustedDecimal adjustedLimit = new AdjustedDecimal(calculatedLimitPrice, decimalInfo.getPriceTickSize());

        validateOcoSellOrder(currentPrice, takeProfitPrice, stopPrice, calculatedLimitPrice);
        validateQuantity(quantity.intValue());

        return binanceHttpClient.executeOCOSellOrder(symbol, adjustedQuantity, adjustedTakeProfit, adjustedStop, adjustedLimit);
    }


    public BinanceOrderResponse executeMarketOrderWithDollar(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars) {
        Double currentPrice = binanceHttpClient.getPrice(symbol);
        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);

        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(calculatedQuantity, decimalInfoDto.getQuantityStepSize());

        validateQuantity(quantityInDollars);
        return binanceHttpClient.executeMarketOrder(symbol, binanceOrderSide, adjustedQuantity);
    }


    public BinanceOrderResponse executeMarketOrderWithQuantity(String symbol, BinanceOrderSide binanceOrderSide, Double quantity) {

        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(quantity, decimalInfoDto.getQuantityStepSize());

        validateQuantity(quantity.intValue());
        return binanceHttpClient.executeMarketOrder(symbol, binanceOrderSide, adjustedQuantity);
    }


    //TODO BURAK mark them as @Transactional if necessary
    public BinanceOrderResponse executeLimitOrder(String symbol, BinanceOrderSide binanceOrderSide, Integer quantityInDollars, Double limitPrice) {
        Double currentPrice = binanceHttpClient.getPrice(symbol);
        validateLimitOrder(binanceOrderSide, limitPrice, currentPrice);
        BinanceDecimalInfoDto decimalInfoDto = getDecimalInfo(symbol);

        Double calculatedQuantity = convertQuantity(quantityInDollars, currentPrice);
        AdjustedDecimal adjustedQuantity = new AdjustedDecimal(calculatedQuantity, decimalInfoDto.getQuantityStepSize());
        AdjustedDecimal adjustedLimitPrice = new AdjustedDecimal(limitPrice, decimalInfoDto.getPriceTickSize());
        validateQuantity(quantityInDollars);

        return binanceHttpClient.executeLimitOrder(symbol, binanceOrderSide, adjustedQuantity, adjustedLimitPrice);
    }

    public List<BinanceCandleStickResponse> getCandles(String symbol, BinanceCandleStickInterval interval, int limit)  {
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


    public List<BinanceTradeResponse> getTradesByOrderId(String symbol, Long orderId) {
        return binanceHttpClient.getTradesByOrderId(symbol, orderId);
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


    public BinanceExchangeInfoResponse getExchangeInfoBySymbol(String symbol) throws HttpClientErrorException {
        return binanceHttpClient.getExchangeInfoBySymbol(symbol);
    }

    public BinanceExchangeInfoResponse getAllExchangeInfo() throws HttpClientErrorException {
        return binanceHttpClient.getAllExchangeInfo();
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

    private void validateLimitOrder(BinanceOrderSide binanceOrderSide, Double price, Double currentPrice) {
        if (binanceOrderSide == BinanceOrderSide.BUY && price * 1.01 > currentPrice) {
            throw new RuntimeException("Limit buy orders cannot be priced more than 1 percent over current price");
        } else if (binanceOrderSide == BinanceOrderSide.SELL && price * 0.99 < currentPrice) {
            throw new RuntimeException("Limit buy orders cannot be priced more than 1 percent less than current price");
        }
    }


    //TODO burak validations
    //stop loss price cannot be 5 percent of alis maliyeti
    private void validateOcoSellOrder(Double currentPrice, Double takeProfitPrice, Double stopPrice, Double limitPrice) {
        if (currentPrice > takeProfitPrice)
            throw new RuntimeException("Current price cannot be greater than takeProfit price for OCO orders");
        if (stopPrice > currentPrice)
            throw new RuntimeException("Stop price cannot be greater than current price price for OCO orders");
        if (limitPrice > currentPrice)
            throw new RuntimeException("Limit price cannot be greater than current price for OCO orders");
        if (limitPrice > stopPrice)
            throw new RuntimeException("Limit price cannot be greater than Stop price for OCO orders");
        if (stopPrice < currentPrice * 0.95)//todo add cannto be 5 percent of alis maliyeti
            throw new RuntimeException("Stop loss cannot be greater than %5 less of current price for OCO orders");
        if (stopPrice * 0.98 > limitPrice)
            throw new RuntimeException("Limit price cannot be less than %2 of stop price for OCO orders");
    }


    private void validateQuantity(Integer quantityInDollars) {
        if (quantityInDollars > binanceTradeProperties.getMaxAllowedDollarsTrade())
            throw new RuntimeException("Order cannot be greater than max allowed quantity");
    }

}
