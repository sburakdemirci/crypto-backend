package com.mtd.crypto.notification.data.response;

import lombok.Data;

@Data
public class TelegramMessage {
    private String message_id;
    private TelegramChat chat;
}

