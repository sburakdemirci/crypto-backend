package com.mtd.crypto.market.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceCancelAllOrdersRequest extends BinanceRequest {

    private String symbol;

   /* @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        Optional.ofNullable(symbol).ifPresent(s -> map.add("symbol", s));
        return map;
    }*/

}