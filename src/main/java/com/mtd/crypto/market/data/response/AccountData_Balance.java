package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountData_Balance {
    private String asset;
    private String free;
    private String locked;
}
