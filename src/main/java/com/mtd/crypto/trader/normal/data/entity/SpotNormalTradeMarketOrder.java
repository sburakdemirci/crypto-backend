package com.mtd.crypto.trader.normal.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderSide;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.persistence.*;
import lombok.*;

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