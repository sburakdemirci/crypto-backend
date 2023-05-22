package com.mtd.crypto.market.data.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder

public class BinanceDecimalInfoDto {
    private Double priceTickSize;
    private Double quantityStepSize;
    private Double minimumOrderInDollars;

}
