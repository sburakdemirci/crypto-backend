package com.mtd.crypto.trader.normal.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.trading.normal.notification.telegram")
public class SpotNormalTradeNotificationConfiguration {

    private String errorChannelId;
    private String infoChannelId;
    private String reportChannelId;

}


