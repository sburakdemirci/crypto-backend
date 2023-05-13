package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class BinanceCurrentPriceResponse {

    @SerializedName("symbol")
    public String symbol;

    @SerializedName("price")
    public Double price;

}
