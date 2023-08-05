package com.mtd.crypto.trader.spot.rest;

import com.mtd.crypto.core.security.annotation.CurrentUser;
import com.mtd.crypto.core.security.configuration.UserPrincipal;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.common.notification.TradeNotificationService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeAdjustRequest;
import com.mtd.crypto.trader.spot.data.request.SpotNormalTradeCreateRequest;
import com.mtd.crypto.trader.spot.data.response.SpotNormalTradeResponse;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeEntryAlgorithm;
import com.mtd.crypto.trader.spot.helper.SpotNormalTradeEstimatedProfitLossCalculator;
import com.mtd.crypto.trader.spot.service.SpotNormalTradeDataService;
import com.mtd.crypto.trader.spot.service.SpotNormalTraderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("spot/normal/trade")
@RequiredArgsConstructor
public class SpotNormalTradeController {

    private final SpotNormalTradeDataService dataService;
    private final BinanceService binanceService;
    private final TradeNotificationService notificationService;
    private final SpotNormalTraderService traderService;

    @PostMapping
    public SpotNormalTradeData save(@RequestBody @Valid SpotNormalTradeCreateRequest spotNormalTradeCreateRequest) {
        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeCreateRequest.getSymbol());

        if (spotNormalTradeCreateRequest.getEntryAlgorithm() == SpotNormalTradeEntryAlgorithm.PRICE_DROP && currentPrice < spotNormalTradeCreateRequest.getEntry()) {
            throw new RuntimeException("Entry price cannot be lower than current price when price drop required!");
        }

        return dataService.save(Optional.empty(), spotNormalTradeCreateRequest);
        //todo test here with mockMVC to get expected validation result for currentPrice
        //TODO add controllerAdvice for binanceException and all other exceptions. Return response entity with exception message
        //Check if coin exists in binance
        //validate stop and limit price
    }

    @PutMapping("{id}")
    public SpotNormalTradeData update(@PathVariable String id, @RequestBody @Valid SpotNormalTradeCreateRequest spotNormalTradeCreateRequest) {
        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeCreateRequest.getSymbol());

        if (spotNormalTradeCreateRequest.getEntryAlgorithm() == SpotNormalTradeEntryAlgorithm.PRICE_DROP && currentPrice < spotNormalTradeCreateRequest.getEntry()) {
            throw new RuntimeException("Entry price cannot be lower than current price when price drop required!");
        }

        return dataService.save(Optional.of(id), spotNormalTradeCreateRequest);
        //todo test here with mockMVC to get expected validation result for currentPrice
        //TODO add controllerAdvice for binanceException and all other exceptions. Return response entity with exception message
        //Check if coin exists in binance
        //validate stop and limit price
    }


    @PatchMapping("{tradeId}/approve")
    public void approveTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {
        SpotNormalTradeData tradeData = dataService.approveTrade(tradeId);
        notificationService.sendInfoMessage(String.format("Trade Approved \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getCurrentStop(), tradeId, userPrincipal.getUsername()));
    }

    //TODO burak partial close


    @PatchMapping("{tradeId}/adjust")
    public void approveTrade(@PathVariable String tradeId, @RequestBody SpotNormalTradeAdjustRequest request) {

        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        request.setAverageEntryPrice(tradeData.getAverageEntryPrice());
        if (tradeData.getTradeStatus() != TradeStatus.IN_POSITION) {
            throw new RuntimeException("Only in position trades can be adjusted");
        }

        dataService.adjustTradeInPosition(tradeId, request);
    }

    @PatchMapping("{tradeId}/close")
    public void approveTrade(@PathVariable String tradeId) {

        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        if (tradeData.getTradeStatus() == TradeStatus.IN_POSITION) {
            traderService.manualClose(tradeId);
        } else {
            throw new RuntimeException("Trade cannot be closed because of status. Trade Status: " + tradeData.getTradeStatus() + " TradeId: " + tradeId);

        }
    }

    @DeleteMapping("{tradeId}")
    public void cancelTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {

        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        if (tradeData.getTradeStatus() == TradeStatus.POSITION_WAITING || tradeData.getTradeStatus() == TradeStatus.APPROVAL_WAITING) {
            dataService.deleteTradeById(tradeId);
            notificationService.sendInfoMessage(String.format("Trade Deleted \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getCurrentStop(), tradeId, userPrincipal.getUsername()));
        } else {
            throw new RuntimeException("Trade cannot be cancelled because of status. Trade Status: " + tradeData.getTradeStatus() + " TradeId: " + tradeId);
        }
    }

    @GetMapping
    public List<SpotNormalTradeResponse> getAll() {

        List<SpotNormalTradeData> activeTrades = dataService.findAllByOrderByTradeStatusAscCreatedTimeAsc();

        return activeTrades.stream().map(trade -> {
            List<SpotNormalTradeMarketOrder> marketOrders = dataService.findAllMarketOrderByParentTrade(trade.getId());
            Double currentPrice = binanceService.getCurrentPrice(trade.getSymbol());

            SpotNormalTradeResponse spotNormalTradeResponse = new SpotNormalTradeResponse();
            spotNormalTradeResponse.setTradeData(trade);
            //TODO do not fetch current price for finished orders. And refactor this logic.
            spotNormalTradeResponse.setCurrentPrice(currentPrice);

            if (trade.getTradeStatus() == TradeStatus.IN_POSITION) {
                spotNormalTradeResponse.setMarketOrders(marketOrders);
                spotNormalTradeResponse.setCollectedProfit(SpotNormalTradeEstimatedProfitLossCalculator.calculateGradualProfits(trade, marketOrders));
                spotNormalTradeResponse.setCurrentEstimatedProfitLoss(SpotNormalTradeEstimatedProfitLossCalculator.calculateCurrentEstimatedProfitLoss(trade, currentPrice));
            }

            List<TradeStatus> finishedTradeStatuses = Arrays.asList(TradeStatus.POSITION_FINISHED, TradeStatus.MANUALLY_CLOSED);

            if (finishedTradeStatuses.contains(trade.getTradeStatus())) {
                spotNormalTradeResponse.setMarketOrders(marketOrders);
                spotNormalTradeResponse.setFinishedProfitLoss(SpotNormalTradeEstimatedProfitLossCalculator.calculateFinishedPositionProfitLoss(marketOrders));
            }
            return spotNormalTradeResponse;
        }).collect(Collectors.toList());
    }

    //Get All By Filter

}
