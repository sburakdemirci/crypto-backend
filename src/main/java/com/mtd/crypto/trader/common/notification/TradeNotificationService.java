package com.mtd.crypto.trader.common.notification;

import com.mtd.crypto.notification.service.TelegramService;
import com.mtd.crypto.trader.spot.configuration.SpotNormalTradeNotificationConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeNotificationService {

    private final TelegramService telegramService;
    private final SpotNormalTradeNotificationConfiguration spotNormalTradeNotificationConfiguration;


    public void sendErrorMessage(String message) {
        telegramService.sendMessage(spotNormalTradeNotificationConfiguration.getErrorChannelId(), message);
    }

    public void sendInfoMessage(String message) {
        telegramService.sendMessage(spotNormalTradeNotificationConfiguration.getInfoChannelId(), message);
    }

    public void sendReportMessage(String message) {
        telegramService.sendMessage(spotNormalTradeNotificationConfiguration.getReportChannelId(), message);
    }


}
