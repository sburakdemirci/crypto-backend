package com.mtd.crypto.trader.normal.scheduled;

import com.mtd.crypto.trader.normal.service.SpotNormalTradeDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotNormalTradeScheduler {

    private final SpotNormalTradeDataService spotNormalTradeDataService;

    //TODO burak make it like 1 hour or more. It will not change frequently
    @Scheduled(cron = "0 * * * * *")
    public void myScheduledMethod() {
        log.info("Evicting exchanceInfo cache with cron");
    }

    //TODO check OCO orders is placed or not
    //TODO check for entrance and profit exit

}