package com.mtd.crypto.market.data.custom;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@AllArgsConstructor
public class AdjustedDecimal {

    private Double value;
    private Double adjuster;

    @Override
    public String toString() {
        BigDecimal bdValue = BigDecimal.valueOf(value);
        BigDecimal bdAdjuster = BigDecimal.valueOf(adjuster);
        BigDecimal adjustedValue = bdValue.divide(bdAdjuster, 0, RoundingMode.DOWN).multiply(bdAdjuster).stripTrailingZeros();
        return adjustedValue.toPlainString();
    }

}
