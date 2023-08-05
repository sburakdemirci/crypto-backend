package com.mtd.crypto.market.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.binance.url.futures")
public class BinanceFuturesApiUrlProperties {

    private String api;
    private Path path;

    @Getter
    @Setter
    public static class Path {
        private String exchangeInfo;
        private String markPrice;
        private String lastPrice;
        private String order;
        private String positionRisk;
        private String adjustLeverage;
        private String changeMarginType;
        private String userTrades;
    }
}


