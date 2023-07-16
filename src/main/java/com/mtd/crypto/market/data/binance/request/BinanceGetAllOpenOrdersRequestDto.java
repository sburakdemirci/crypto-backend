package com.mtd.crypto.market.data.binance.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceGetAllOpenOrdersRequestDto extends BinanceRequestBase {

    private String symbol;

}