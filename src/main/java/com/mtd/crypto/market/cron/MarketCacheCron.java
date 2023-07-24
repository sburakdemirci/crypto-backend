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

    @Scheduled(fixedRate = 3600000)
    public void evictSpotExchangeInfoCache() {
        log.info("Evicting exchanceInfo cache with cron");
        marketCacheService.evictSpotExchangeInfoCache();
    }


    @Scheduled(fixedRate = 3600000)
    public void evictFuturesExchangeInfoCache() {
        log.info("Evicting exchanceInfo cache with cron");
        marketCacheService.evictFuturesExchangeInfoCache();
    }

}