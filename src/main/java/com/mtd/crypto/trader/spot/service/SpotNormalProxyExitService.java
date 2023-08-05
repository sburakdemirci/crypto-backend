package com.mtd.crypto.trader.spot.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.common.notification.TradeNotificationService;
import com.mtd.crypto.trader.spot.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.spot.pipeline.visitor.SpotVisitor;
import com.mtd.crypto.trader.spot.pipeline.visitor.sell.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@LoggableClass
public class SpotNormalProxyExitService extends AbstractTrader {

    private final SpotNormalTradeDataService dataService;
    private final SpotSellHalfGradualProfitVisitor spotSellHalfGradualProfitVisitor;
    private final SpotSellNormalTakeProfitVisitor spotSellNormalTakeProfitVisitor;
    private final SpotSellScalingOutTakeProfitVisitor spotSellScalingOutTakeProfitVisitor;
    private final SpotSellStopLossInsuranceVisitor spotSellStopLossInsuranceVisitor;
    private final SpotSellStopLossVisitor spotSellStopLossVisitor;


    protected SpotNormalProxyExitService(BinanceService binanceService, TradeNotificationService notificationService,
                                         SpotNormalTraderService traderService,
                                         SpotNormalTradeDataService dataService,
                                         SpotSellHalfGradualProfitVisitor spotSellHalfGradualProfitVisitor, SpotSellNormalTakeProfitVisitor spotSellNormalTakeProfitVisitor, SpotSellScalingOutTakeProfitVisitor spotSellScalingOutTakeProfitVisitor, SpotSellStopLossInsuranceVisitor spotSellStopLossInsuranceVisitor, SpotSellStopLossVisitor spotSellStopLossVisitor) {
        super(binanceService, notificationService, traderService);
        this.dataService = dataService;
        this.spotSellHalfGradualProfitVisitor = spotSellHalfGradualProfitVisitor;
        this.spotSellNormalTakeProfitVisitor = spotSellNormalTakeProfitVisitor;
        this.spotSellScalingOutTakeProfitVisitor = spotSellScalingOutTakeProfitVisitor;
        this.spotSellStopLossInsuranceVisitor = spotSellStopLossInsuranceVisitor;
        this.spotSellStopLossVisitor = spotSellStopLossVisitor;
    }

    public void execute() {
        List<SpotNormalTradeData> allByTradeStatus = dataService.findAllByTradeStatus(TradeStatus.IN_POSITION);
        allByTradeStatus.forEach(this::execute);
    }


    @Override
    public List<SpotVisitor> generateAlgorithm(SpotNormalTradeData tradeData) {
        List<SpotVisitor> spotVisitors = new ArrayList<>();
        spotVisitors.add(spotSellStopLossVisitor);


        return spotVisitors;
    }
}
