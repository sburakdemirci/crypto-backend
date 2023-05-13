package com.mtd.crypto.notification.data.response;

import lombok.Data;

@Data
public class TelegramMessageResponse {
    private boolean ok;
    private TelegramMessage result;
}


