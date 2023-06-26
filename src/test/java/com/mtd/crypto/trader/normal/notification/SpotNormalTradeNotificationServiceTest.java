package com.mtd.crypto.trader.normal.notification;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("dev")
class SpotNormalTradeNotificationServiceTest {

    @Autowired
    SpotNormalTradeNotificationService spotNormalTradeNotificationService;
    @Test
    void sendErrorMessage() {
        spotNormalTradeNotificationService.sendErrorMessage("error message");
    }

    @Test
    void sendInfoMessage() {
        spotNormalTradeNotificationService.sendInfoMessage("info message");
    }

    @Test
    void sendReportMessage() {
        spotNormalTradeNotificationService.sendReportMessage("report message");

    }
}