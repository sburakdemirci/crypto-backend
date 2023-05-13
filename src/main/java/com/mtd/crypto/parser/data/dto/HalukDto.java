package com.mtd.crypto.parser.data.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HalukDto {
    private String coin;
    private double entryPrice;
    private double stopPrice;
    private double takeProfit1;
    private Double takeProfit2;
    private boolean reverse;

}

