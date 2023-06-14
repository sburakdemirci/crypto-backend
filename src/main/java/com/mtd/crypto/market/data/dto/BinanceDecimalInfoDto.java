package com.mtd.crypto.market.data.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class BinanceDecimalInfoDto {
    private Double priceTickSize;
    private Double quantityStepSize;
    private Double minimumOrderInDollars;

}
