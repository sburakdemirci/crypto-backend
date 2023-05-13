package com.mtd.crypto.trader.data.entity;


import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.trader.enumarator.TradeStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class OrderHistory extends EntityAuditBase {

    //TODO BURAK BUTUN DATAYI ENCRYPTE TUTMAYI DUSUN HATTA YAP
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String coin;
    private Double average;
    private Double stop;
    private Double limit;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @Builder
    public OrderHistory(String coin, Double entry, Double tp1, Double tp2, Double stop, TradeStatus status) {
        this.coin = coin;

        this.stop = stop;
        this.status = status;
    }
}
