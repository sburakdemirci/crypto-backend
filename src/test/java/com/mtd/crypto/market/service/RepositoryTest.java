package com.mtd.crypto.market.service;


import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeDataRepository;
import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

@SpringBootTest
@ActiveProfiles("dev")
public class RepositoryTest {

    @Autowired
    SpotNormalTradeDataRepository spotNormalTradeDataRepository;

    @Autowired
    SpotNormalTradeDataService dataService;


    @Test
    public void save() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("FIRST")
                .baseTradingSymbol("USDT")
                .source(TradeSource.HALUK)
                .tradeStatus(TradeStatus.APPROVAL_WAITING)
                .build();

        SpotNormalTradeData spotNormalTradeData2 = SpotNormalTradeData.builder()
                .symbol("SECOND")
                .baseTradingSymbol("SOME")
                .source(TradeSource.HALUK)
                .tradeStatus(TradeStatus.APPROVAL_WAITING)
                .build();

/*
        dataService.transactionalTest(spotNormalTradeData,spotNormalTradeData2);
*/

    }
}
