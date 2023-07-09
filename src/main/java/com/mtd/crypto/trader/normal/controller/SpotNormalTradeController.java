package com.mtd.crypto.trader.normal.controller;

import com.mtd.crypto.core.security.annotation.CurrentUser;
import com.mtd.crypto.core.security.configuration.UserPrincipal;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.notification.SpotNormalTradeNotificationService;
import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("spot/normal")
@RequiredArgsConstructor
public class SpotNormalTradeController {

    private final SpotNormalTradeDataService dataService;
    private final BinanceService binanceService;
    private final SpotNormalTradeNotificationService notificationService;

    @PostMapping("data")
    public SpotNormalTradeData save(@RequestBody @Valid SpotNormalTradeDto spotNormalTradeDto) {
        Double currentPrice = binanceService.getCurrentPrice(spotNormalTradeDto.getSymbol());

        if (spotNormalTradeDto.isPriceDropRequired() && currentPrice < spotNormalTradeDto.getEntry()) {
            throw new RuntimeException("Entry price cannot be lower than current price when price drop required!");
        }

        return dataService.createTradeData(spotNormalTradeDto);
        //todo test here with mockMVC to get expected validation result for currentPrice
        //TODO add controllerADvice for binanceException and all other exceptions. Return response entity with exception message
        //Check if coin exists in binance
        //validate stop and limit price
    }

    @PatchMapping("trade/{tradeId}/approve")
    public void approveTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {
        dataService.approveTrade(tradeId);
        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        notificationService.sendInfoMessage(String.format("Trade Approved \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getStop(), tradeId, userPrincipal.getUsername()));
    }

    @DeleteMapping("trade/{tradeId}/cancel")
    public void cancelTrade(@PathVariable String tradeId, @CurrentUser UserPrincipal userPrincipal) {
        dataService.cancelTradeBeforePosition(tradeId);
        SpotNormalTradeData tradeData = dataService.findById(tradeId);
        notificationService.sendInfoMessage(String.format("Trade Cancelled \nCoin: %s\nEntry:%,.4f  TakeProfit:%,.4f  Stop:%,.4f\nTradeId: %s \nApproved By: %s", tradeData.getSymbol(), tradeData.getEntry(), tradeData.getTakeProfit(), tradeData.getStop(), tradeId, userPrincipal.getUsername()));
    }

    @GetMapping("trade/active")
    public List<SpotNormalTradeData> getTradesByFilter() {
        List<TradeStatus> tradeStatuses = Arrays.asList(
                TradeStatus.APPROVAL_WAITING,
                TradeStatus.POSITION_WAITING,
                //  TradeStatus.CANCELLED_BEFORE_POSITION,
             //   TradeStatus.EXPIRED,
                TradeStatus.IN_POSITION);
                // TradeStatus.CANCELLED_IN_POSITION,
                //    TradeStatus.POSITION_FINISHED_WITH_PROFIT,
                //    TradeStatus.POSITION_FINISHED_WITH_LOSS);
        return dataService.findTradesByStatusesOrderByStatusAndCreatedTime(tradeStatuses);
    }


    //cancel trade should be checking if trade is in position. If its in position it should first execute sell order, than update trade data as cancelled.

    //Get All By Filter
    //Approve Trade
    //Cancel Trade in position. First make market sell then mark order as cancelled in position

}
