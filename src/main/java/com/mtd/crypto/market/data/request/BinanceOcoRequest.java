package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
import com.mtd.crypto.market.data.enumarator.BinanceOrderTimeInForce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BinanceOcoRequest extends BinanceRequest {

    private String symbol;
    private BigDecimal quantity;
    private Double stopPrice;
    private Double price;
    private Double stopLimitPrice;
    private BinanceOrderSide side;
    private BinanceOrderTimeInForce stopLimitTimeInForce;


    @Override
    public MultiValueMap<String, String> toMultiValueMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("symbol", symbol);
        map.add("side", side.toString());
        //todo burak butun price olan yerlere bu ozelligi kazandir
        map.add("quantity", quantity.setScale(4, RoundingMode.HALF_UP).toString());
        map.add("price", price.toString());
        map.add("stopPrice", stopPrice.toString());
        map.add("stopLimitPrice", stopLimitPrice.toString());
        map.add("stopLimitTimeInForce", stopLimitTimeInForce.toString());

        return map;
    }

}

