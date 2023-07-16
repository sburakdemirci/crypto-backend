package com.mtd.crypto.trader.settings.data.repository;

import com.mtd.crypto.trader.settings.data.entity.SystemSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SettingsRepository extends JpaRepository<SystemSettings, String> {

    public Optional<SystemSettings> findFirstByOrderByIdAsc();
}
