package com.mtd.crypto.trader.data.entity;


import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.trader.enumarator.TradeStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class TradeData extends EntityAuditBase {

    //TODO BURAK BUTUN DATAYI ENCRYPTE TUTMAYI DUSUN HATTA YAP
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String coin;
    private Double entry;
    private Double tp1;
    private Double tp2;
    private Double stop;
    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @Builder
    public TradeData(String coin, Double entry, Double tp1, Double tp2, Double stop, TradeStatus status) {
        this.coin = coin;
        this.entry = entry;
        this.tp1 = tp1;
        this.tp2 = tp2;
        this.stop = stop;
        this.status = status;
    }
}
