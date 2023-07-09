package com.mtd.crypto.market.data.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.json.JSONArray;


import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BinanceCandleStickResponse {
    private Long openTime;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
    private Long closeTime;
    private Double quoteAssetVolume;
    private Integer numberOfTrades;
    private Double takerBuyBaseAssetVolume;
    private Double takerBuyQuoteAssetVolume;

    public static List<BinanceCandleStickResponse> parse(String responseToParse)  {
        org.json.JSONArray jsonArray = new JSONArray(responseToParse);
        List<BinanceCandleStickResponse> candles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONArray candleArray = jsonArray.getJSONArray(i);
            BinanceCandleStickResponse candle = new BinanceCandleStickResponse();
            candle.setOpenTime(candleArray.getLong(0));
            candle.setOpen(candleArray.getDouble(1));
            candle.setHigh(candleArray.getDouble(2));
            candle.setLow(candleArray.getDouble(3));
            candle.setClose(candleArray.getDouble(4));
            candle.setVolume(candleArray.getDouble(5));
            candle.setCloseTime(candleArray.getLong(6));
            candle.setQuoteAssetVolume(candleArray.getDouble(7));
            candle.setNumberOfTrades(candleArray.getInt(8));
            candle.setTakerBuyBaseAssetVolume(candleArray.getDouble(9));
            candle.setTakerBuyQuoteAssetVolume(candleArray.getDouble(10));
            candles.add(candle);
        }
        return candles;
    }
}
