package com.mtd.crypto.market.cron;

import com.mtd.crypto.market.service.MarketCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketCacheCron {

    private final MarketCacheService marketCacheService;

    //TODO burak make it like 1 hour or more. It will not change frequently
    @Scheduled(fixedRate = 3600000)
    public void evictExchangeInfoCache() {
        log.info("Evicting exchanceInfo cache with cron");
        marketCacheService.evictExchangeInfoCache();
    }

}