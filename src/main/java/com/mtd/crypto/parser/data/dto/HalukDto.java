package com.mtd.crypto.parser.data.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HalukDto {
    private String coin;
    private Double entryPrice;
    private Double stopPrice;
    private Double takeProfit1;
    private Double takeProfit2;
    private boolean reverse;

}

