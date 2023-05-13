package com.mtd.crypto.market.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.binance.url")
public class BinanceApiUrlProperties {

    private String orderApi;
    private String walletApi;
    private String priceApi;
}
