package com.mtd.crypto.trader.spot.data.request;

import com.mtd.crypto.trader.spot.enumarator.SpotNormalTradeEntryAlgorithm;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpotNormalTradeCreateRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @Positive(message = "Entry must be greater than zero")
    private Double entry;

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double stop;

    @NotNull
    private SpotNormalTradeEntryAlgorithm entryAlgorithm;

    private boolean gradualSelling;

    @NotNull
    private Integer positionAmountInDollar;

    private String notes;

    @AssertTrue(message = "Stop price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanStop() {
        return takeProfit > stop;
    }

    @AssertTrue(message = "Stop price cannot be higher than entry price")
    private boolean isEntryHigherThanStop() {
        return entryAlgorithm == SpotNormalTradeEntryAlgorithm.CURRENT_PRICE || entry > stop;
    }

    @AssertTrue(message = "Entry price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanEntry() {
        return entryAlgorithm == SpotNormalTradeEntryAlgorithm.CURRENT_PRICE || takeProfit > entry;
    }

    @AssertTrue(message = "Stop price is too low for entry price")
    private boolean isHighLoss() {
        return entryAlgorithm == SpotNormalTradeEntryAlgorithm.CURRENT_PRICE || stop > entry * 0.9;
    }

}
