package com.mtd.crypto.trader.normal.data.entity;

import com.mtd.crypto.market.data.enumarator.binance.BinanceOrderSide;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeMarketOrderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

import static jakarta.persistence.TemporalType.TIMESTAMP;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotNormalTradeMarketOrder {
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

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private Instant createdTime;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Instant updatedTime;

}