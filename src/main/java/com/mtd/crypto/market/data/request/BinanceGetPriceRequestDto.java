package com.mtd.crypto.market.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceGetPriceRequestDto extends BinanceRequestBase {
    private String symbol;


}