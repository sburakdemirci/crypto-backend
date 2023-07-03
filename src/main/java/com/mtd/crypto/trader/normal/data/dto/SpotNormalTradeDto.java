package com.mtd.crypto.trader.normal.data.dto;

import com.mtd.crypto.trader.common.enumarator.TradeSource;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpotNormalTradeDto {

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
    private TradeSource source;

    @Min(1)
    @Max(30)
    private Integer walletPercentage;

    @AssertTrue(message = "Stop price cannot be higher than take profit price")
    private boolean isTakeProfitHigherThanStop() {
        return takeProfit > stop;
    }

    @AssertTrue(message = "Stop price cannot be higher than take entry price")
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
