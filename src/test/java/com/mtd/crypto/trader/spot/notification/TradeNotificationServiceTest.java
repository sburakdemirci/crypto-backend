package com.mtd.crypto.trader.spot.notification;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)

@ActiveProfiles("dev")
class TradeNotificationServiceTest {

    @Autowired
    TradeNotificationService tradeNotificationService;

    @Test
    void sendErrorMessage() {
        tradeNotificationService.sendErrorMessage("Test Error message");
    }

    @Test
    void sendInfoMessage() {
        tradeNotificationService.sendInfoMessage("Test Info message");
    }

    @Test
    void sendReportMessage() {
        tradeNotificationService.sendReportMessage("Test Report message");
    }
}