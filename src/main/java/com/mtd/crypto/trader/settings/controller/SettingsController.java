package com.mtd.crypto.trader.settings.controller;


import com.mtd.crypto.trader.settings.data.entity.SystemSettings;
import com.mtd.crypto.trader.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("settings")
@RequiredArgsConstructor
public class SettingsController {
    private final SettingsService settingsService;

    @GetMapping
    public SystemSettings getSettings() {
        return settingsService.getSettings();
    }

    @PatchMapping("normal-spot-trade")
    public SystemSettings changeNormalTradeStatus(@RequestParam boolean value) {
        return settingsService.setNormalTradeActivationStatus(value);
    }

}
