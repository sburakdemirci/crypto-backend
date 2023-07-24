package com.mtd.crypto.trader.spot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.trading.normal.strategy")
public class SpotNormalTradingStrategyConfiguration {
    private Double priceDropSafeEntryPercentage;
    private Double partialExitPercentageStep;
}


