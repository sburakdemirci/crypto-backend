package com.mtd.crypto.market.data.binance.request.futures;

import com.mtd.crypto.market.data.binance.request.BinanceRequestBase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BinanceAdjustLeverageRequestDto extends BinanceRequestBase {

    private String symbol;
    @Min(1)
    @Max(3)
    private Integer leverage;
}
