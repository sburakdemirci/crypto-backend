package com.mtd.crypto.notification.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.telegram")
public class TelegramSecretProperties {

    private String apiKey;
    private String baseUrl;
}
