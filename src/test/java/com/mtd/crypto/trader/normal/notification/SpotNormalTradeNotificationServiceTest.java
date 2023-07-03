package com.mtd.crypto.trader.normal.notification;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@RunWith(MockitoJUnitRunner.class)

@ActiveProfiles("dev")
class SpotNormalTradeNotificationServiceTest {

    @Autowired
    SpotNormalTradeNotificationService spotNormalTradeNotificationService;

    @Test
    void sendErrorMessage() {
        spotNormalTradeNotificationService.sendErrorMessage("Test Error message");
    }

    @Test
    void sendInfoMessage() {
        spotNormalTradeNotificationService.sendInfoMessage("Test Info message");
    }

    @Test
    void sendReportMessage() {
        spotNormalTradeNotificationService.sendReportMessage("Test Report message");

    }
}