package com.mtd.crypto.market.configuration;

import com.mtd.crypto.market.service.BinanceService;
import com.mtd.crypto.trader.spot.notification.TradeNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MarketConfiguration {

    private final TradeNotificationService notificationService;
    private final BinanceService binanceService;


    @Bean
    @ConditionalOnProperty(prefix = "app.binance", name = "startup-health-check", havingValue = "true")
    public void testBinanceHealth() {
        try {
            binanceService.executeHealthCheck();
            notificationService.sendInfoMessage("System Startup.\nBinance Health Check is Successful!");
        } catch (Exception e) {
            notificationService.sendErrorMessage("System Startup.\nBinance Health Check is Failed! \nError:" + e.getMessage());
        }
    }
}
