package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceOcoRequestDto extends BinanceRequestBase {

    private String symbol;
    private Double quantity;
    private Double stopPrice;
    private Double price;
    private Double stopLimitPrice;
    private BinanceOrderSide side;
    private BinanceOrderTimeInForce stopLimitTimeInForce;

}

