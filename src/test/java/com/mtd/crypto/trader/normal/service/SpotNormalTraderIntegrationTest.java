package com.mtd.crypto.trader.normal.service;


import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.normal.data.dto.SpotNormalTradeDto;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@SpringBootTest
@Testcontainers
@ActiveProfiles("dev")
public class SpotNormalTraderIntegrationTest {

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0")).withDatabaseName("cryptotest")
            .withUsername("root")
            .withPassword("test");
    @Autowired
    SpotNormalTraderProxyService spotNormalTraderProxyService;
    @Autowired
    SpotNormalTradeDataService spotNormalTradeDataService;
    @MockBean
    private BinanceService binanceService;

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    public void test() {
        SpotNormalTradeDto spotNormalTradeDto = SpotNormalTradeDto
                .builder()
                .symbol("BTCUSTD")
                .baseTradingSymbol("USDT")
                .entry(100.0)
                .takeProfit(110.0)
                .stop(90.0)
                .isPriceDropRequired(true)
                .source(TradeSource.BURAK)
                .build();
        SpotNormalTradeData tradeData = spotNormalTradeDataService.createTradeData(spotNormalTradeDto);

        spotNormalTradeDataService.approveTrade(tradeData.getId());
        List<SpotNormalTradeData> tradesByStatus = spotNormalTradeDataService.findTradesByStatus(TradeStatus.APPROVAL_WAITING);
        Mockito.when(binanceService.getCurrentPrice(Mockito.anyString())).thenReturn(31.31);
        spotNormalTraderProxyService.checkAndEnterPositions();

    }


}
