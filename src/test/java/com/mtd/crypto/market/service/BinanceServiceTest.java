package com.mtd.crypto.market.service;


import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BinanceServiceTest {


    private static final String TEST_SYMBOL = "BTCUSDT";
    @Autowired
    private BinanceService binanceService;

    //TODO IMPORTANT!! Run this test daily with test environment yaml.

}

