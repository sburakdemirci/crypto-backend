package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceGetCandleRequestDto extends BinanceRequestBase {
    private String symbol;
    private BinanceCandleStickInterval interval;
    private int limit;
    private Long endTime;


}