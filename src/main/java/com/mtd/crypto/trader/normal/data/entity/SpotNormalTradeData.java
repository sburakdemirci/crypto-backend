package com.mtd.crypto.trader.normal.data.entity;

import com.mtd.crypto.trader.common.enumarator.TradeSource;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
public class SpotNormalTradeData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TradeStatus tradeStatus = TradeStatus.APPROVAL_WAITING;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotBlank(message = "Base trading symbol is required")
    private String baseTradingSymbol;

    @Positive(message = "Entry must be greater than zero")
    private Double entry;

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double stop;

    private boolean isPriceDropRequired;

    @NotNull(message = "Source is required")
    @Enumerated(EnumType.STRING)
    private TradeSource source;

    @Min(1)
    @Max(40)
    private Integer walletPercentage = 10; // Default value, can be overridden (min 1, max 100)

    @CreatedDate
    @Temporal(TIMESTAMP)
    @Column(updatable = false)
    private Instant createdTime;

    private Double totalQuantity;
    private Double averageEntryPrice;
    private Double quantityLeftInPosition;

    private Instant approvedAt;
    private Instant cancelledAt;
    private Instant positionStartedAt;
    private Instant positionFinishedAt;

    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Instant updatedTime;

}
