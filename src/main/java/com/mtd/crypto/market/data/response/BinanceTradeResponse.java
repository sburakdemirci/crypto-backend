package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class BinanceTradeResponse {

    private String symbol;
    @JsonProperty("id")
    private Long tradeId;
    private Long orderId;
    private Long orderListId;
    private String price;
    private String qty;
    private String quoteQty;
    private String commission;
    private String commissionAsset;
    private Long time;
    private boolean isBuyer;
    private boolean isMaker;
    private boolean isBestMatch;
}



