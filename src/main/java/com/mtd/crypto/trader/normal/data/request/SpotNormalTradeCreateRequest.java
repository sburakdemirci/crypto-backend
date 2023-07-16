package com.mtd.crypto.trader.normal.data.request;

import jakarta.validation.constraints.*;
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

    private String quoteAsset;

    @Positive(message = "Entry must be greater than zero")
    private Double entry;

    @Positive(message = "Take profit must be greater than zero")
    private Double takeProfit;

    @Positive(message = "Stop must be greater than zero")
    private Double stop;

    private boolean priceDropRequired;
    private boolean gradualSelling;

    private boolean burak;

    @Min(1)
    @Max(29)
    private Integer walletPercentage = 30;

    @AssertTrue(message = "Stop price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanStop() {
        return takeProfit > stop;
    }

    @AssertTrue(message = "Stop price cannot be higher than entry price")
    private boolean isEntryHigherThanStop() {
        return entry > stop;
    }

    @AssertTrue(message = "Entry price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanEntry() {
        return takeProfit > entry;
    }

    @AssertTrue(message = "Stop price is too low for entry price")
    private boolean isHighLoss() {
        return stop > entry * 0.9;
    }
}
