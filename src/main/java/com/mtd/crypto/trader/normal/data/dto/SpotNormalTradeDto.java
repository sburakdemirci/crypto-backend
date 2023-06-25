package com.mtd.crypto.trader.normal.data.dto;

import com.mtd.crypto.trader.common.enumarator.TradeSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SpotNormalTradeDto {

    //TODO validate if symbol is exists on binance
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

    @NotBlank(message = "Source is required")
    private TradeSource source;

    @Min(1)
    @Max(40)
    private Integer walletPercentage;


}
