package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.common.notification.TradeNotificationService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import com.mtd.crypto.trader.spot.pipeline.visitor.buy.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@LoggableClass
public class SpotNormalProxyEnterService extends AbstractTrader {

    private final SpotNormalTradeDataService dataService;
    private final SpotBuyEnterCheckWalletVisitor spotBuyEnterCheckWalletVisitor;
    private final SpotBuyEnterLastPriceVisitor spotBuyEnterLastPriceVisitor;
    private final SpotBuyEnterWithoutCheckingPriceVisitor spotBuyEnterWithoutCheckingPriceVisitor;
    private final SpotBuyLossAndProfitCompareVisitor spotBuyLossAndProfitCompareVisitor;
    private final SpotNormalBuyEnter4HoursCandleCloseVisitor spotNormalBuyEnter4HoursCandleCloseVisitor;

    protected SpotNormalProxyEnterService(BinanceService binanceService, TradeNotificationService notificationService,
                                          SpotNormalTraderService traderService,
                                          SpotNormalTradeDataService dataService,
                                          SpotBuyEnterCheckWalletVisitor spotBuyEnterCheckWalletVisitor,
                                          SpotBuyEnterLastPriceVisitor spotBuyEnterLastPriceVisitor,
                                          SpotBuyEnterWithoutCheckingPriceVisitor spotBuyEnterWithoutCheckingPriceVisitor,
                                          SpotBuyLossAndProfitCompareVisitor spotBuyLossAndProfitCompareVisitor,
                                          SpotNormalBuyEnter4HoursCandleCloseVisitor spotNormalBuyEnter4HoursCandleCloseVisitor) {
        super(binanceService, notificationService, traderService);
        this.dataService = dataService;
        this.spotBuyEnterCheckWalletVisitor = spotBuyEnterCheckWalletVisitor;
        this.spotBuyEnterLastPriceVisitor = spotBuyEnterLastPriceVisitor;
        this.spotBuyEnterWithoutCheckingPriceVisitor = spotBuyEnterWithoutCheckingPriceVisitor;
        this.spotBuyLossAndProfitCompareVisitor = spotBuyLossAndProfitCompareVisitor;
        this.spotNormalBuyEnter4HoursCandleCloseVisitor = spotNormalBuyEnter4HoursCandleCloseVisitor;
    }

    public void execute() {
        List<SpotNormalTradeData> allByTradeStatus = dataService.findAllByTradeStatus(TradeStatus.POSITION_WAITING);
        allByTradeStatus.forEach(this::execute);
    }


    @Override
    public List<SpotVisitor> generateAlgorithm(SpotNormalTradeData tradeData) {
        List<SpotVisitor> spotVisitors = new ArrayList<>();
        spotVisitors.add(spotBuyEnterCheckWalletVisitor);
        spotVisitors.add(spotBuyLossAndProfitCompareVisitor);

        switch (tradeData.getEntryAlgorithm()) {
            case LAST_PRICE -> spotVisitors.add(spotBuyEnterLastPriceVisitor);
            case PRICE_DROP -> spotVisitors.add(spotBuyEnterLastPriceVisitor);
            case CURRENT_PRICE -> spotVisitors.add(spotBuyEnterWithoutCheckingPriceVisitor);
            case CANDLE_4_HOURS_CLOSE -> spotVisitors.add(spotNormalBuyEnter4HoursCandleCloseVisitor);
        }

        return spotVisitors;
    }
}
