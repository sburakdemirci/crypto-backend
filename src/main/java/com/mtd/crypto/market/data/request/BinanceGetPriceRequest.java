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
public class BinanceGetPriceRequest extends BinanceRequest {
    private String symbol;



 /*   @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("symbol", symbol);
        return body;
    }*/

}