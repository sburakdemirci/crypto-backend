package com.mtd.crypto.trader.spot.rest;


import com.mtd.crypto.trader.settings.service.SettingsService;
import com.mtd.crypto.trader.spot.service.SpotNormalProxyEnterService;
import com.mtd.crypto.trader.spot.service.SpotNormalProxyExitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cron")
@RequiredArgsConstructor
//@SecurityRequirement(name = "bearerAuth")
public class SpotNormalTradeCronController {


    private final SpotNormalProxyEnterService proxyEnterService;
    private final SpotNormalProxyExitService proxyExitService;
    private final SettingsService settingsService;

    @GetMapping("spot-normal-enter")
    public void checkAndEnterPositions() {
        if (settingsService.getSettings().isSpotNormalTradeActivated()) {
            proxyEnterService.execute();
        }
    }

    @GetMapping("spot-normal-exit")
    public void checkAndExitPositions() {
        if (settingsService.getSettings().isSpotNormalTradeActivated()) {
            proxyExitService.execute();
        }
    }


}
