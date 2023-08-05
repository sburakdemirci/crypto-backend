package com.mtd.crypto.market.data.binance.request;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceGetTradesRequestDto extends BinanceRequestBase {
    private String symbol;
    private Long orderId;

}