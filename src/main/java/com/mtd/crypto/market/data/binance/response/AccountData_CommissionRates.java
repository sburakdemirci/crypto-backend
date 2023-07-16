package com.mtd.crypto.market.data.binance.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountData_CommissionRates {
    private String maker;
    private String taker;
    private String buyer;
    private String seller;
}
