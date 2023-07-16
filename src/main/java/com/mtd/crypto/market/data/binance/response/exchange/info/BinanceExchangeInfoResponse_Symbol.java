package com.mtd.crypto.market.data.binance.response.exchange.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mtd.crypto.market.data.binance.binance.BinanceOrderType;
import com.mtd.crypto.market.data.binance.binance.BinanceSymbolStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceExchangeInfoResponse_Symbol {
    private String symbol;
    private BinanceSymbolStatus status;
    private String baseAsset;
    private int baseAssetPrecision;
    private String quoteAsset;
    private int quotePrecision;
    private int quoteAssetPrecision;
    private int baseCommissionPrecision;
    private int quoteCommissionPrecision;
    private List<BinanceOrderType> orderTypes;
    private boolean icebergAllowed;
    private boolean ocoAllowed;
    private boolean quoteOrderQtyMarketAllowed;
    private boolean allowTrailingStop;
    private boolean cancelReplaceAllowed;
    private boolean spotTradingAllowed;
    private boolean marginTradingAllowed;
    @Setter(AccessLevel.NONE)
    private List<LinkedHashMap> filters;
    @Setter(AccessLevel.NONE)
    private BinanceFilter binanceFilter;


    public void setFilters(List<LinkedHashMap> filters) {
        this.binanceFilter = BinanceFilter.parse(filters);
        this.filters = filters;
    }
}

