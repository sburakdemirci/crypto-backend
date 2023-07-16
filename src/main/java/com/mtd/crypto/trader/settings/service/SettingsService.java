package com.mtd.crypto.trader.settings.service;

import com.mtd.crypto.trader.settings.data.entity.SystemSettings;
import com.mtd.crypto.trader.settings.data.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository repository;

    public SystemSettings getSettings() {
        return repository.findFirstByOrderByIdAsc().orElseGet(() -> repository.save(new SystemSettings()));
    }

    public SystemSettings setNormalTradeActivationStatus(boolean isActive) {
        SystemSettings settings = getSettings();
        settings.setSpotNormalTradeActivated(isActive);
        return repository.save(settings);
    }
}
