package com.mtd.crypto.trader.spot.data.entity;

import com.mtd.crypto.core.configuration.EntityAuditBase;
import com.mtd.crypto.trader.common.enumarator.TradeStatus;
import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeEntryAlgorithm;
import jakarta.persistence.*;
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
            "WHEN 'IN_POSITION' THEN 2 " +
            "WHEN 'POSITION_WAITING' THEN 3 " +
            "WHEN 'MANUALLY_CLOSED' THEN 4 " +
            "WHEN 'IN_POSITION' THEN 5 " +
            "ELSE 6 " +
            "END")
    private TradeStatus tradeStatus;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    private Double entry;

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double initialStop;

    @Positive(message = "Stop must be greater than zero")
    private Double currentStop;

    private Integer positionAmountInDollar;

    @Enumerated(EnumType.STRING)
    private SpotNormalTradeEntryAlgorithm entryAlgorithm;

    //todo exit algoritmalari icin de ayni sekilde enum olustur. Exit stratejilerinden pipeline olustur her bir strateji icin. Bu algoritmalari bir interface ile bagla. Ve sonunda o algoritmayi secerek devam ettir Map<ExitAlgorithmType, SpotExitAlgorihmInterface> gibi


    private boolean gradualProfit;
    private boolean scalingOutProfit;


    private Double averageEntryPrice;
    private Double totalQuantity;
    private Double quantityLeftInPosition;

    //INFO
    private String notes;

    /**
     * Info is a field to represent info for this coin. For example if the wallet does not have enought usdt to enter a position, coin will be endup in approval_waiting position with an info says "No available amount to execute this order."
     */
    private String info;
    private Instant approvedAt;
    private Instant startedAt;
    private Instant finishedAt;

}
