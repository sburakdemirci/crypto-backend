package com.mtd.crypto.trader.normal.data.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpotNormalTradeAdjustRequest {

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double stop;

    @Positive(message = "Average entry price must be greater than zero")

    @JsonIgnore
    private Double averageEntryPrice;

    private boolean gradualSelling;


    @AssertTrue(message = "Stop price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanStop() {
        return takeProfit > stop;
    }

    @AssertTrue(message = "Stop price cannot be higher than entry price")
    private boolean isEntryHigherThanStop() {
        return averageEntryPrice > stop;
    }

    @AssertTrue(message = "Entry price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanEntry() {
        return takeProfit > averageEntryPrice;
    }

    @AssertTrue(message = "Stop price is too low for entry price")
    private boolean isHighLoss() {
        return stop > averageEntryPrice * 0.9;
    }
}
