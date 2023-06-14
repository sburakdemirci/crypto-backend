package com.mtd.crypto.market.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.binance.trade")
public class BinanceTradeProperties {
    private Integer maxAllowedDollarsTrade;
}
