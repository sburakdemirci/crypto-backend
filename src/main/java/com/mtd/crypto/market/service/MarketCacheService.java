package com.mtd.crypto.market.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class MarketCacheService {
    @CacheEvict(value = "spotExchangeInfo", allEntries = true)
    public void evictSpotExchangeInfoCache() {
    }

    @CacheEvict(value = "futuresExchangeInfo", allEntries = true)
    public void evictFuturesExchangeInfoCache() {
    }
}