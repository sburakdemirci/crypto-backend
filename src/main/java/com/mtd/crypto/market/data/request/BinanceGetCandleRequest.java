package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceCandleStickInterval;
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
public class BinanceGetCandleRequest extends BinanceRequest {
    private String symbol;
    private BinanceCandleStickInterval interval;
    private int limit;
    private Long endTime;

/*    @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("symbol", symbol);
        body.add("interval", interval.getIntervalId());
        body.add("limit", String.valueOf(limit));
        body.add("endTime", endTime.toString());
        return body;
    }*/

}