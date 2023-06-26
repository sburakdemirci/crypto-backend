package com.mtd.crypto.trader.normal.data.entity;

import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotNormalTradeMarketOrder extends EntityAuditBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)

    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTradeId")
    private SpotNormalTradeData parentTrade;

    private Long binanceOrderId;

    @Enumerated(EnumType.STRING)
    private BinanceOrderSide side;

    private Double quantity;
    private Double averagePrice;

    @Enumerated(EnumType.STRING)
    private SpotNormalTradeMarketOrderType type;

}