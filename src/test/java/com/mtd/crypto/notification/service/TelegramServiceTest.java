package com.mtd.crypto.notification.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("dev")

class TelegramServiceTest {

    @Autowired
    private TelegramService telegramService;


    @Test
    public void sendMessage() {
        telegramService.sendMessage("-1001710984089","helooo");
    }


}