package com.mtd.crypto.trader.normal.service;

import com.mtd.crypto.core.aspect.LoggableClass;
import com.mtd.crypto.market.service.BinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@LoggableClass
@RequiredArgsConstructor
public class SpotNormalTraderService {

    private final SpotNormalTradeDataService spotNormalTradeDataService;
    private final BinanceService binanceService;


    public void getTradesReadyToEnter() {

    }

    //TODO increase stoplimit when you take profit of 1/3


    public void getTradesReadyForPartialTakeProfit() {

    }


    public void getTradesReadyFor() {

    }


}
