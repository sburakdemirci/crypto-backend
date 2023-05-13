package com.mtd.crypto.market.data.request;

import com.mtd.crypto.market.data.enumarator.BinanceOrderType;
import com.mtd.crypto.market.data.enumarator.BinanceOrderSide;
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
public class BinanceMarketBuyRequest extends BinanceRequest {
    private String symbol;
    private BinanceOrderSide side;
    private BinanceOrderType type;
    private Integer quoteOrderQty;

   /* @Override
    public MultiValueMap<String,String> toMultiValueMap(){
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("symbol", symbol);
        body.add("side", side.toString());
        body.add("type", type.toString());
        body.add("quoteOrderQty", quoteOrderQty.toString());
        return body;
    }*/

}