package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.binance.BinanceCandleStickInterval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceGetCandleRequestDto extends BinanceRequestBase {
    private String symbol;
    private BinanceCandleStickInterval interval;
    private int limit;
    private Long endTime;


    @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();

        multiValueMap.add("symbol", symbol);
        multiValueMap.add("interval", interval.getIntervalId());
        multiValueMap.add("limit", String.valueOf(limit));
        multiValueMap.add("endTime", endTime.toString());
        return multiValueMap;
    }
}