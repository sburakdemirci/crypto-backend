package com.mtd.crypto.trader.spot.rest;

import com.mtd.crypto.core.security.annotation.CurrentUser;
import com.mtd.crypto.core.security.configuration.UserPrincipal;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeAdjustRequest;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeCreateRequest;
import com.mtd.crypto.trader.spot.data.response.SpotNormalTradeResponse;
import com.mtd.crypto.trader.spot.helper.SpotNormalTradeEstimatedProfitLossCalculator;
import com.mtd.crypto.trader.spot.notification.TradeNotificationService;
import com.mtd.crypto.trader.spot.service.SpotNormalTradeDataService;
import com.mtd.crypto.trader.spot.service.SpotNormalTraderProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("spot/normal/trade")
@RequiredArgsConstructor
public class SpotNormalTradeController {

    private final SpotNormalTradeDataService dataService;
    private final BinanceService binanceService;
    private final TradeNotificationService notificationService;
    private final SpotNormalTraderProxyService proxyService;

    @PostMapping
    public SpotNormalTradeData save(@RequestBody @Valid SpotNormalTradeCreateRequest spotNormalTradeCreateRequest) {
        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeCreateRequest.getSymbol());

        if (spotNormalTradeCreateRequest.isPriceDropRequired() && currentPrice < spotNormalTradeCreateRequest.getEntry()) {
            throw new RuntimeException("Entry price cannot be lower than current price when price drop required!");
        }

        return dataService.createTradeData(spotNormalTradeCreateRequest);
        //todo test here with mockMVC to get expected validation result for currentPrice
        //TODO add controllerAdvice for binanceException and all other exceptions. Return response entity with exception message
        //Check if coin exists in binance
        //validate stop and limit price
    }

    @PatchMapping("{tradeId}/approve")
    public void approveTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {
        dataService.approveTrade(tradeId);
        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        notificationService.sendInfoMessage(String.format("Trade Approved \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getStop(), tradeId, userPrincipal.getUsername()));
    }


    @PatchMapping("{tradeId}/adjust")
    public void approveTrade(@PathVariable String tradeId, @RequestBody SpotNormalTradeAdjustRequest request) {

        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        request.setAverageEntryPrice(tradeData.getAverageEntryPrice());
        if (tradeData.getTradeStatus() != TradeStatus.IN_POSITION) {
            throw new RuntimeException("Only in position trades can be adjusted");
        }

        dataService.adjustTradeInPosition(tradeId, request);
    }


    @DeleteMapping("{tradeId}/cancel")
    public void cancelTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {

        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        proxyService.cancelTrade(tradeData);
        notificationService.sendInfoMessage(String.format("Trade Cancelled \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getStop(), tradeId, userPrincipal.getUsername()));
    }

    @GetMapping("all")
    public List<SpotNormalTradeResponse> getAllTrades() {

        List<SpotNormalTradeData> activeTrades = dataService.findAllByOrderByTradeStatusAscCreatedTimeAsc();

        return activeTrades.stream().map(trade -> {
            List<SpotNormalTradeMarketOrder> marketOrders = dataService.findAllMarketOrderByParentTrade(trade.getId());
            Double currentPrice = binanceService.getCurrentPrice(trade.getSymbol());

            SpotNormalTradeResponse spotNormalTradeResponse = new SpotNormalTradeResponse();
            spotNormalTradeResponse.setTradeData(trade);
            spotNormalTradeResponse.setCurrentPrice(currentPrice);

            if (trade.getTradeStatus() == TradeStatus.IN_POSITION) {
                spotNormalTradeResponse.setMarketOrders(marketOrders);
                spotNormalTradeResponse.setCollectedProfit(SpotNormalTradeEstimatedProfitLossCalculator.calculateGradualProfits(trade, marketOrders));
                spotNormalTradeResponse.setCurrentEstimatedProfitLoss(SpotNormalTradeEstimatedProfitLossCalculator.calculateCurrentEstimatedProfitLoss(trade, currentPrice));
            }

            List<TradeStatus> finishedTradeStatuses = Arrays.asList(TradeStatus.CLOSED_IN_POSITION, TradeStatus.POSITION_FINISHED_WITH_PROFIT, TradeStatus.POSITION_FINISHED_WITH_LOSS);

            if (finishedTradeStatuses.contains(trade.getTradeStatus())) {
                spotNormalTradeResponse.setMarketOrders(marketOrders);
                spotNormalTradeResponse.setFinishedProfitLoss(SpotNormalTradeEstimatedProfitLossCalculator.calculateFinishedPositionProfitLoss(marketOrders));
            }
            return spotNormalTradeResponse;
        }).collect(Collectors.toList());
    }


    //cancel trade should be checking if trade is in position. If its in position it should first execute sell order, than update trade data as cancelled.

    //Get All By Filter
    //Approve Trade
    //Cancel Trade in position. First make market sell then mark order as cancelled in position

}
