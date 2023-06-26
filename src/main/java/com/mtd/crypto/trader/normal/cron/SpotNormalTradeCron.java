package com.mtd.crypto.trader.normal.cron;

import com.mtd.crypto.trader.normal.service.SpotNormalTraderProxyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class SpotNormalTradeCron {

    private final SpotNormalTraderProxyService spotNormalTraderProxyService;

    @Scheduled(cron = "${app.trading.normal.cron.position-enter}")
    public void checkAndEnterPositions() {
        spotNormalTraderProxyService.checkAndEnterPositions();
    }

    @Scheduled(cron = "${app.trading.normal.cron.position-exit}")
    public void checkAndExitPositions() {
        spotNormalTraderProxyService.checkAndExitPositions();
    }
}
