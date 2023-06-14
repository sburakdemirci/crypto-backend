package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserAssetResponse {

    private String asset;
    private Double free;
    private Double locked;
    private Double freeze;
    private Double withdrawing;
    private Double ipoable;
    private Double btcValuation;
}
