package com.mtd.crypto.trader.spot.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.market.data.binance.enumarator.BinanceOrderSide;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "parentTrade")
public class SpotNormalTradeMarketOrder extends EntityAuditBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    private String id;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTradeId")
    private SpotNormalTradeData parentTrade;

    private Long binanceOrderId;

    @Enumerated(EnumType.STRING)
    private BinanceOrderSide side;

    private Double quantity;
    private Double averagePrice;
    private Double commission;

    @Enumerated(EnumType.STRING)
    private SpotNormalTradeMarketOrderType type;

}