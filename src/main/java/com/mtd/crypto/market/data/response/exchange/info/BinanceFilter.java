package com.mtd.crypto.market.data.response.exchange.info;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class BinanceFilter {

    private BinanceFilter_PriceFilter priceFilter;
    private BinanceFilter_LotSizeFilter lotSizeFilter;
    private BinanceFilter_IcebergPartsFilter icebergPartsFilter;
    private BinanceFilter_MarketLotSizeFilter marketLotSizeFilter;
    private BinanceFilter_TrailingDeltaFilter trailingDeltaFilter;
    private BinanceFilter_PercentPriceBySideFilter percentPriceBySideFilter;
    private BinanceFilter_NotionalFilter notionalFilter;
    private BinanceFilter_MaxNumOrdersFilter maxNumOrdersFilter;
    private BinanceFilter_MaxNumAlgoOrdersFilter maxNumAlgoOrdersFilter;


    //todo use cache

    public static BinanceFilter parse(List<LinkedHashMap> linkedHashMaps) {

        BinanceFilter binanceFilter = new BinanceFilter();
        ObjectMapper mapper = new ObjectMapper();

        for (LinkedHashMap linkedHashMap : linkedHashMaps) {

            switch (linkedHashMap.get("filterType").toString()) {
                case "PRICE_FILTER":
                    binanceFilter.priceFilter = mapper.convertValue(linkedHashMap, BinanceFilter_PriceFilter.class);
                    break;

                case "LOT_SIZE":
                    binanceFilter.lotSizeFilter = mapper.convertValue(linkedHashMap, BinanceFilter_LotSizeFilter.class);
                    break;

                case "ICEBERG_PARTS":
                    binanceFilter.icebergPartsFilter = mapper.convertValue(linkedHashMap, BinanceFilter_IcebergPartsFilter.class);
                    break;

                case "MARKET_LOT_SIZE":
                    binanceFilter.marketLotSizeFilter = mapper.convertValue(linkedHashMap, BinanceFilter_MarketLotSizeFilter.class);
                    break;

                case "TRAILING_DELTA":
                    binanceFilter.trailingDeltaFilter = mapper.convertValue(linkedHashMap, BinanceFilter_TrailingDeltaFilter.class);
                    break;

                case "PERCENT_PRICE_BY_SIDE":
                    binanceFilter.percentPriceBySideFilter = mapper.convertValue(linkedHashMap, BinanceFilter_PercentPriceBySideFilter.class);
                    break;

                case "NOTIONAL":
                    binanceFilter.notionalFilter = mapper.convertValue(linkedHashMap, BinanceFilter_NotionalFilter.class);
                    break;

                case "MAX_NUM_ORDERS":
                    binanceFilter.maxNumOrdersFilter = mapper.convertValue(linkedHashMap, BinanceFilter_MaxNumOrdersFilter.class);
                    break;

                case "MAX_NUM_ALGO_ORDERS":
                    binanceFilter.maxNumAlgoOrdersFilter = mapper.convertValue(linkedHashMap, BinanceFilter_MaxNumAlgoOrdersFilter.class);
                    break;

                default:
                    // Handle unrecognized filter type
                    break;
            }
        }
        return binanceFilter;
    }
}



