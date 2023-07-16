package com.mtd.crypto.trader.settings.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SystemSettings {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private String id;

    private boolean spotNormalTradeActivated;


}
