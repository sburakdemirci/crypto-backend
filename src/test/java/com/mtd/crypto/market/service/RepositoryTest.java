package com.mtd.crypto.market.service;


import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeData;
import com.mtd.crypto.trader.normal.data.entity.SpotNormalTradeMarketOrder;
import com.mtd.crypto.trader.normal.data.repository.SpotNormalTradeDataRepository;
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


    @Test
    public void save() {

        SpotNormalTradeData spotNormalTradeData = SpotNormalTradeData.builder()
                .symbol("BTCUSDt")
                .build();

        SpotNormalTradeMarketOrder spotNormalTradeMarketOrder = SpotNormalTradeMarketOrder
                .builder()
                .build();

        spotNormalTradeData.setMarketOrders(Arrays.asList(spotNormalTradeMarketOrder));
        SpotNormalTradeData save = spotNormalTradeDataRepository.save(spotNormalTradeData);


    }
}
