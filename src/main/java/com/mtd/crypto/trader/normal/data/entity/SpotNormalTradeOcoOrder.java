package com.mtd.crypto.trader.normal.data.entity;

import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeOcoOrderCancelReason;
import com.mtd.crypto.trader.normal.enumarator.SpotNormalTradeOcoOrderStatus;
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
public class SpotNormalTradeOcoOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parentTradeId")
    private SpotNormalTradeData parentTrade;

    private Long binanceOcoOrderListId;

    private Long binanceTakeProfitOrderId;
    private Long binanceStopLossOrderId;

    //Quantity ve price'lar cok onemli. Eger coinde stop noktasini vs yukari cekip kar alindiysa, bunun kaydi buralarda tutulacak
    private Double quantity;
    private Double takeProfitPrice;
    private Double stopPrice;
    private Double stopLimitPrice;

    @Enumerated(EnumType.STRING)
    private SpotNormalTradeOcoOrderStatus status;

    @Enumerated(EnumType.STRING)
    private SpotNormalTradeOcoOrderCancelReason cancelReason;

    private Instant cancelledAt;

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private Instant createdTime;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Instant updatedTime;

}