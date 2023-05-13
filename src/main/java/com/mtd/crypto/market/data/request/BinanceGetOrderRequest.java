package com.mtd.crypto.market.data.request;

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
public class BinanceGetOrderRequest extends BinanceRequest {
    private String symbol;
    private Long orderId;

/*    @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("symbol", symbol);
        body.add("orderId", orderId.toString());
        return body;
    }*/

}