package com.mtd.crypto.trader.spot.data.entity;

import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpotNormalTradeData extends EntityAuditBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @OrderBy("CASE status " +
            "WHEN 'APPROVAL_WAITING' THEN 1 " +
            "WHEN 'POSITION_WAITING' THEN 2 " +
            "WHEN 'CANCELLED_BEFORE_POSITION' THEN 3 " +
            "WHEN 'EXPIRED' THEN 4 " +
            "WHEN 'IN_POSITION' THEN 5 " +
            "WHEN 'CLOSED_IN_POSITION' THEN 6 " +
            "WHEN 'POSITION_FINISHED_WITH_PROFIT' THEN 7 " +
            "WHEN 'POSITION_FINISHED_WITH_LOSS' THEN 8 " +
            "ELSE 9 " +
            "END")
    private TradeStatus tradeStatus;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    private String quoteAsset;

    private Double entry;
    private boolean enterCurrentPrice;

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double stop;

    private boolean priceDropRequired;
    private boolean gradualProfit;

    @NotNull(message = "Source is required")
    @Enumerated(EnumType.STRING)
    private TradeSource source;

    private Double walletPercentage;

    private Double totalQuantity;
    private Double averageEntryPrice;
    private Double quantityLeftInPosition;
    private String notes;

    private Instant approvedAt;
    private Instant cancelledAt;
    private Instant positionStartedAt;
    private Instant positionFinishedAt;


}
