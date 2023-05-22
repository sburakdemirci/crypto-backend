package com.mtd.crypto.market.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class MarketCacheService {
    @CacheEvict(value = "exchangeInfo", allEntries = true)
    public void evictExchangeInfoCache() {
    }
}