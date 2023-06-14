package com.mtd.crypto.market.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.binance.url")
public class BinanceApiUrlProperties {

    private String api;
    private List<String> backupApis;
    private Path path;

    @Getter
    @Setter
    public static class Path {
        private String systemStatus;
        private String price;
        private String klines;
        private String exchangeInfo;
        private String normalOrder;
        private String openOrders;
        private String ocoOrder;
        private String ocoOrderList;
        private String ocoOpenOrderList;
        private String ocoAllOrderList;
        private String wallet;
        private String account;
        private String accountSnapshot;
        private String userAsset;
        private String myTrades;
    }
}


