package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceAccountInformationResponse {
    private BigDecimal makerCommission;
    private BigDecimal takerCommission;
    private BigDecimal buyerCommission;
    private BigDecimal sellerCommission;
    private boolean canTrade;
    private boolean canWithdraw;
    private boolean canDeposit;
    private long updateTime;
    private String accountType;
    private String accountStatus;
    private List<Balance> balances;
    private List<Asset> permissions;

    @Data
    public static class Balance {
        private String asset;
        private BigDecimal free;
        private BigDecimal locked;
    }

    @Data
    public static class Asset {
        private String asset;
        private boolean enableWithdraw;
        private boolean enableDeposit;
    }
}
